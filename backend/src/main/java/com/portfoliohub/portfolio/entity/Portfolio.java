package com.portfoliohub.portfolio.entity;

import com.portfoliohub.auth.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "portfolios")
public class Portfolio {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "owner_user_id", nullable = false) private User owner;
    @Column(nullable = false, length = 120) private String title;
    @Column(nullable = false, length = 80) private String slug;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private PortfolioStatus status = PortfolioStatus.DRAFT;
    @Column(name = "active_template_version_id") private UUID activeTemplateVersionId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "current_draft_revision_id") private PortfolioRevision currentDraftRevision;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "published_revision_id") private PortfolioRevision publishedRevision;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;
    @Column(name = "published_at") private Instant publishedAt;
    @PrePersist void onCreate(){Instant now=Instant.now();createdAt=now;updatedAt=now;normalize();}
    @PreUpdate void onUpdate(){updatedAt=Instant.now();normalize();}
    private void normalize(){if(slug!=null)slug=slug.trim().toLowerCase();if(title!=null)title=title.trim();}
    public UUID getId(){return id;} public User getOwner(){return owner;} public String getTitle(){return title;} public String getSlug(){return slug;} public PortfolioStatus getStatus(){return status;} public UUID getActiveTemplateVersionId(){return activeTemplateVersionId;} public PortfolioRevision getCurrentDraftRevision(){return currentDraftRevision;} public PortfolioRevision getPublishedRevision(){return publishedRevision;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;} public Instant getPublishedAt(){return publishedAt;}
    public void setOwner(User v){owner=v;} public void setTitle(String v){title=v;} public void setSlug(String v){slug=v;} public void setStatus(PortfolioStatus v){status=v;} public void setActiveTemplateVersionId(UUID v){activeTemplateVersionId=v;} public void setCurrentDraftRevision(PortfolioRevision v){currentDraftRevision=v;} public void setPublishedRevision(PortfolioRevision v){publishedRevision=v;} public void setPublishedAt(Instant v){publishedAt=v;}
}
