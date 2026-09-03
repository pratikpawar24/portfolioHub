package com.portfoliohub.auth.repository;

import com.portfoliohub.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshToken t set t.revokedAt = :when where t.userId = :userId and t.revokedAt is null and t.expiresAt > :when")
    int revokeAllActiveForUser(@Param("userId") UUID userId, @Param("when") Instant when);
}
