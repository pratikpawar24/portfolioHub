package com.portfoliohub.auth.service;

import com.portfoliohub.auth.entity.EmailVerificationOtp;
import com.portfoliohub.auth.entity.User;
import com.portfoliohub.auth.repository.EmailVerificationOtpRepository;
import com.portfoliohub.auth.repository.UserRepository;
import com.portfoliohub.common.api.ApiException;
import com.portfoliohub.common.email.TransactionalEmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class EmailVerificationService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final EmailVerificationOtpRepository otpRepository;
    private final TransactionalEmailSender emailSender;
    private final long expiryMinutes;
    private final long resendCooldownSeconds;
    private final int maxAttempts;
    private final String otpSecret;

    public EmailVerificationService(
            UserRepository userRepository,
            EmailVerificationOtpRepository otpRepository,
            TransactionalEmailSender emailSender,
            @Value("${app.email.verification.expiry-minutes:10}") long expiryMinutes,
            @Value("${app.email.verification.resend-cooldown-seconds:60}") long resendCooldownSeconds,
            @Value("${app.email.verification.max-attempts:5}") int maxAttempts,
            @Value("${app.email.verification.otp-secret:${app.security.jwt.secret}}") String otpSecret) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.emailSender = emailSender;
        this.expiryMinutes = expiryMinutes;
        this.resendCooldownSeconds = resendCooldownSeconds;
        this.maxAttempts = maxAttempts;
        this.otpSecret = otpSecret;
    }

    @Transactional
    public void issueFor(User user) {
        if (user.isEmailVerified()) return;

        Instant now = Instant.now();
        otpRepository.findTopByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(user.getId())
                .ifPresent(current -> {
                    if (current.getCreatedAt() != null
                            && Duration.between(current.getCreatedAt(), now).getSeconds() < resendCooldownSeconds) {
                        throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "OTP_RESEND_TOO_SOON",
                                "Please wait before requesting another verification code");
                    }
                    current.setUsedAt(now);
                    otpRepository.save(current);
                });

        String otp = String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000));
        EmailVerificationOtp entity = new EmailVerificationOtp();
        entity.setUser(user);
        entity.setCodeHash(hash(user.getId(), otp));
        entity.setExpiresAt(now.plusSeconds(expiryMinutes * 60));
        otpRepository.save(entity);

        emailSender.sendEmailVerificationOtp(user.getEmail(), user.getDisplayName(), otp, expiryMinutes);
    }

    @Transactional
    public User verify(String email, String otp) {
        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> invalidOtp());

        if (user.isEmailVerified()) {
            return user;
        }

        EmailVerificationOtp entity = otpRepository.findTopByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> invalidOtp());

        Instant now = Instant.now();
        if (!entity.getExpiresAt().isAfter(now)) {
            entity.setUsedAt(now);
            otpRepository.save(entity);
            throw new ApiException(HttpStatus.GONE, "OTP_EXPIRED", "Verification code has expired");
        }

        if (entity.getAttempts() >= maxAttempts) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "OTP_ATTEMPTS_EXCEEDED", "Too many verification attempts");
        }

        entity.setAttempts(entity.getAttempts() + 1);
        if (!MessageDigest.isEqual(entity.getCodeHash().getBytes(StandardCharsets.UTF_8), hash(user.getId(), otp).getBytes(StandardCharsets.UTF_8))) {
            otpRepository.save(entity);
            throw invalidOtp();
        }

        entity.setUsedAt(now);
        otpRepository.save(entity);
        user.setEmailVerifiedAt(now);
        return userRepository.save(user);
    }

    @Transactional
    public void resend(String email) {
        userRepository.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT))
                .ifPresent(user -> {
                    if (!user.isEmailVerified()) issueFor(user);
                });
    }

    private String hash(UUID userId, String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((otpSecret + ":" + userId + ":" + otp).getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(64);
            for (byte b : bytes) result.append(String.format(Locale.ROOT, "%02x", b));
            return result.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash verification code", ex);
        }
    }

    private static ApiException invalidOtp() {
        return new ApiException(HttpStatus.BAD_REQUEST, "INVALID_OTP", "Invalid verification code");
    }
}
