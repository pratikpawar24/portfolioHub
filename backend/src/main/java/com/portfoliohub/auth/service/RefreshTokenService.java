package com.portfoliohub.auth.service;

import com.portfoliohub.auth.entity.RefreshToken;
import com.portfoliohub.auth.entity.User;
import com.portfoliohub.auth.repository.RefreshTokenRepository;
import com.portfoliohub.common.api.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class RefreshTokenService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenRepository repository;
    private final long refreshTokenDays;

    public RefreshTokenService(
            RefreshTokenRepository repository,
            @Value("${app.security.jwt.refresh-token-days:30}") long refreshTokenDays) {
        this.repository = repository;
        this.refreshTokenDays = refreshTokenDays;
    }

    @Transactional
    public IssuedRefreshToken issue(User user) {
        String raw = randomToken();
        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setTokenHash(hash(raw));
        entity.setExpiresAt(Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS));
        repository.save(entity);
        return new IssuedRefreshToken(raw, entity.getExpiresAt());
    }

    @Transactional
    public void revoke(String rawToken) {
        RefreshToken current = repository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> invalidToken());
        if (current.getRevokedAt() == null) {
            current.setRevokedAt(Instant.now());
            repository.save(current);
        }
    }

    @Transactional
    public RotationResult rotate(String rawToken) {
        RefreshToken current = repository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> invalidToken());

        Instant now = Instant.now();
        if (current.getRevokedAt() != null || !current.getExpiresAt().isAfter(now)
                || !current.getUser().getStatus().name().equals("ACTIVE")) {
            throw invalidToken();
        }

        current.setRevokedAt(now);
        String nextRaw = randomToken();
        RefreshToken next = new RefreshToken();
        next.setUser(current.getUser());
        next.setTokenHash(hash(nextRaw));
        next.setExpiresAt(now.plus(refreshTokenDays, ChronoUnit.DAYS));
        repository.save(current);
        repository.save(next);
        return new RotationResult(current.getUser(), nextRaw);
    }

    private static ApiException invalidToken() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "Invalid or expired refresh token");
    }

    private static String randomToken() {
        byte[] bytes = new byte[48];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(64);
            for (byte b : digest) result.append(String.format("%02x", b));
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    public record IssuedRefreshToken(String token, Instant expiresAt) {}
    public record RotationResult(User user, String refreshToken) {}
}
