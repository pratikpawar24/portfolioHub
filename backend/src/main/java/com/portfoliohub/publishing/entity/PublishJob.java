package com.portfoliohub.publishing.entity;

import com.portfoliohub.portfolio.entity.Portfolio;
import com.portfoliohub.portfolio.entity.PortfolioRevision;
import com.portfoliohub.template.entity.TemplateVersion;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "publishing_jobs", indexes = {
        @Index(name = "idx_publishing_jobs_portfolio_created", columnList = "portfolio_id,created_at desc"),
        @Index(name = "idx_publishing_jobs_status_created", columnList = "status,created_at desc")
})
public class PublishJob {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "revision_id", nullable = false)
    private PortfolioRevision revision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_version_id")
    private TemplateVersion templateVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PublishJobStatus status = PublishJobStatus.QUEUED;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "artifact_path", length = 1000)
    private String artifactPath;

    @Column(name = "public_path", length = 500)
    private String publicPath;

    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @Column(name = "error_message", length = 4000)
    private String errorMessage;

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    public UUID getId() { return id; }
    public Portfolio getPortfolio() { return portfolio; }
    public PortfolioRevision getRevision() { return revision; }
    public TemplateVersion getTemplateVersion() { return templateVersion; }
    public PublishJobStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getArtifactPath() { return artifactPath; }
    public String getPublicPath() { return publicPath; }
    public String getContentHash() { return contentHash; }
    public String getErrorMessage() { return errorMessage; }

    public void setPortfolio(Portfolio portfolio) { this.portfolio = portfolio; }
    public void setRevision(PortfolioRevision revision) { this.revision = revision; }
    public void setTemplateVersion(TemplateVersion templateVersion) { this.templateVersion = templateVersion; }
    public void setStatus(PublishJobStatus status) { this.status = status; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public void setArtifactPath(String artifactPath) { this.artifactPath = artifactPath; }
    public void setPublicPath(String publicPath) { this.publicPath = publicPath; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
