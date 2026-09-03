package com.portfoliohub.auth.service;

import com.portfoliohub.auth.api.AuthDtos;
import com.portfoliohub.auth.domain.RefreshToken;
import com.portfoliohub.auth.repository.RefreshTokenRepository;
import com.portfoliohub.auth.security.JwtService;
import com.portfoliohub.users.domain.User;
import com.portfoliohub.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.portfoliohub.common.api.ApiException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Duration refreshTokenTtl;
    private final Duration accessTokenTtl;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       @Qualifier("refreshTokenTtl") Duration refreshTokenTtl,
                       @Qualifier("accessTokenTtl") Duration accessTokenTtl) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenTtl = refreshTokenTtl;
        this.accessTokenTtl = accessTokenTtl;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String email = normalizeEmail(request.email());
        String username = request.username().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", "An account with this email already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new ApiException(HttpStatus.CONFLICT, "USERNAME_ALREADY_EXISTS", "This username is already taken");
        }

        User user = userRepository.save(new User(
                email,
                username,
                request.displayName().trim(),
                passwordEncoder.encode(request.password())
        ));

        return issueTokens(user);
    }

    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password");
        }
        if (user.getStatus() != com.portfoliohub.users.domain.UserStatus.ACTIVE) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ACCOUNT_INACTIVE", "Account is not active");
        }

        return issueTokens(user);
    }

    @Transactional
    public AuthDtos.AuthResponse refresh(AuthDtos.RefreshRequest request) {
        String tokenHash = hashToken(request.refreshToken());
        RefreshToken existing = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "Invalid refresh token"));
        Instant now = Instant.now();

        if (!existing.isActive(now)) {
            if (existing.getRevokedAt() != null) {
                refreshTokenRepository.revokeAllActiveForUser(existing.getUserId(), now);
            }
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "Refresh token is expired or revoked");
        }

        User user = userRepository.findById(existing.getUserId())
                .filter(u -> u.getStatus() == com.portfoliohub.users.domain.UserStatus.ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "ACCOUNT_INACTIVE", "Account is not active"));

        // Revoke before issuing the replacement to enforce rotation.
        AuthDtos.AuthResponse response = issueTokens(user);
        UUID replacementId = refreshTokenRepository.findByTokenHash(hashToken(response.refreshToken()))
                .map(RefreshToken::getId)
                .orElse(null);
        existing.revoke(now, replacementId);
        refreshTokenRepository.save(existing);
        return response;
    }

    @Transactional
    public void logout(AuthDtos.LogoutRequest request) {
        refreshTokenRepository.findByTokenHash(hashToken(request.refreshToken()))
                .filter(token -> token.getRevokedAt() == null)
                .ifPresent(token -> {
                    token.revoke(Instant.now(), null);
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional(readOnly = true)
    public AuthDtos.UserResponse currentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .filter(u -> u.getStatus() == com.portfoliohub.users.domain.UserStatus.ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
        return toUserResponse(user);
    }

    private AuthDtos.AuthResponse issueTokens(User user) {
        String refreshToken = randomToken();
        refreshTokenRepository.save(new RefreshToken(
                Objects.requireNonNull(user.getId()),
                hashToken(refreshToken),
                Instant.now().plus(refreshTokenTtl)
        ));

        return new AuthDtos.AuthResponse(
                "Bearer",
                jwtService.generateAccessToken(user),
                accessTokenTtl.getSeconds(),
                refreshToken,
                toUserResponse(user)
        );
    }

    private static AuthDtos.UserResponse toUserResponse(User user) {
        return new AuthDtos.UserResponse(
                user.getId(), user.getEmail(), user.getUsername(), user.getDisplayName(),
                user.getRole().name(), user.getStatus().name(), user.isEmailVerified(), user.getCreatedAt()
        );
    }

    private static String normalizeEmail(String raw) {
        return raw.trim().toLowerCase();
    }

    private static String randomToken() {
        byte[] bytes = new byte[48];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hashToken(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash refresh token", e);
        }
    }
}
