package com.portfoliohub.portfolios.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "portfolios", indexes = {
        @Index(name = "idx_portfolios_owner_user_id", columnList = "owner_user_id"),
        @Index(name = "idx_portfolios_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_portfolios_owner_slug", columnNames = {"owner_user_id", "slug"})
})
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Column(nullable = false, length = 80)
    private String title;

    @Column(nullable = false, length = 80)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PortfolioStatus status;

    @Column(name = "active_template_version_id")
    private UUID activeTemplateVersionId;

    @Column(name = "current_draft_revision_id")
    private UUID currentDraftRevisionId;

    @Column(name = "published_revision_id")
    private UUID publishedRevisionId;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Portfolio() {}

    public Portfolio(UUID ownerUserId, String title, String slug) {
        this.ownerUserId = ownerUserId;
        this.title = title;
        this.slug = slug;
        this.status = PortfolioStatus.DRAFT;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOwnerUserId() { return ownerUserId; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public PortfolioStatus getStatus() { return status; }
    public UUID getActiveTemplateVersionId() { return activeTemplateVersionId; }
    public UUID getCurrentDraftRevisionId() { return currentDraftRevisionId; }
    public UUID getPublishedRevisionId() { return publishedRevisionId; }
    public Instant getPublishedAt() { return publishedAt; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void updateMetadata(String title, String slug) {
        this.title = title;
        this.slug = slug;
    }

    public void setCurrentDraftRevisionId(UUID revisionId) {
        this.currentDraftRevisionId = revisionId;
        // Editing must never publish or unpublish a portfolio implicitly.
        // The existing lifecycle state is preserved until an explicit lifecycle command.
    }

    public void publishRevision(UUID revisionId) {
        this.publishedRevisionId = revisionId;
        this.currentDraftRevisionId = revisionId;
        this.publishedAt = Instant.now();
        this.status = PortfolioStatus.PUBLISHED;
    }

    public void unpublish() {
        this.status = PortfolioStatus.UNPUBLISHED;
    }

    public void archive() {
        this.status = PortfolioStatus.ARCHIVED;
    }

    public void setActiveTemplateVersionId(UUID activeTemplateVersionId) {
        this.activeTemplateVersionId = activeTemplateVersionId;
    }
}
