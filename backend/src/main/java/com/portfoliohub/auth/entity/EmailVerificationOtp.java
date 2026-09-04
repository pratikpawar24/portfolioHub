package com.portfoliohub.auth.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_verification_otps", indexes = {
        @Index(name = "idx_email_verification_otps_user_created", columnList = "user_id,created_at"),
        @Index(name = "idx_email_verification_otps_expires_at", columnList = "expires_at")
})
public class EmailVerificationOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_email_verification_otps_user"))
    private User user;

    @Column(name = "code_hash", nullable = false, length = 128)
    private String codeHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public String getCodeHash() { return codeHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getUsedAt() { return usedAt; }
    public int getAttempts() { return attempts; }
    public Instant getCreatedAt() { return createdAt; }

    public void setUser(User user) { this.user = user; }
    public void setCodeHash(String codeHash) { this.codeHash = codeHash; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public void setUsedAt(Instant usedAt) { this.usedAt = usedAt; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
}
