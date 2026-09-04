package com.portfoliohub.auth.repository;

import com.portfoliohub.auth.entity.EmailVerificationOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationOtpRepository extends JpaRepository<EmailVerificationOtp, UUID> {
    Optional<EmailVerificationOtp> findTopByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(UUID userId);
}
