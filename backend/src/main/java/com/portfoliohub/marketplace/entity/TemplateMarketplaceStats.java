package com.portfoliohub.marketplace.entity;

import com.portfoliohub.template.entity.Template;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "template_marketplace_stats")
public class TemplateMarketplaceStats {
    @Id
    private UUID templateId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false)
    private long likeCount;

    @Column(nullable = false)
    private long favoriteCount;

    @Column(nullable = false)
    private long usageCount;

    @Column(nullable = false)
    private long forkCount;

    @Column(nullable = false)
    private long remixCount;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getTemplateId() { return templateId; }
    public Template getTemplate() { return template; }
    public long getLikeCount() { return likeCount; }
    public long getFavoriteCount() { return favoriteCount; }
    public long getUsageCount() { return usageCount; }
    public long getForkCount() { return forkCount; }
    public long getRemixCount() { return remixCount; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setTemplate(Template template) { this.template = template; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
    public void setFavoriteCount(long favoriteCount) { this.favoriteCount = favoriteCount; }
    public void setUsageCount(long usageCount) { this.usageCount = usageCount; }
    public void setForkCount(long forkCount) { this.forkCount = forkCount; }
    public void setRemixCount(long remixCount) { this.remixCount = remixCount; }
}
