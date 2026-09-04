package com.portfoliohub.creator.entity;

import com.portfoliohub.auth.entity.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "creator_profiles", uniqueConstraints = @UniqueConstraint(name = "uk_creator_profiles_user", columnNames = "user_id"))
public class CreatorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 1000)
    private String bio;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        normalize();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
        normalize();
    }

    private void normalize() {
        if (bio != null) bio = bio.trim();
        if (avatarUrl != null) avatarUrl = avatarUrl.trim();
        if (websiteUrl != null) websiteUrl = websiteUrl.trim();
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getWebsiteUrl() { return websiteUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUser(User user) { this.user = user; }
    public void setBio(String bio) { this.bio = bio; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
}
