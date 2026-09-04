package com.portfoliohub.template.entity;

import com.portfoliohub.auth.entity.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "templates")
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id")
    private User creator;

    @Column(nullable = false, length = 100)
    private String slug;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(length = 60)
    private String category;

    @Column(nullable = false, length = 100)
    private String license;

    @Column(name = "repository_url", length = 500)
    private String repositoryUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TemplateVisibility visibility = TemplateVisibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TemplateStatus status = TemplateStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_template_id")
    private Template parentTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_template_id")
    private Template originalTemplate;

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
        if (slug != null) slug = slug.trim().toLowerCase();
        if (name != null) name = name.trim();
        if (license != null) license = license.trim();
        if (category != null) category = category.trim();
    }

    public UUID getId() { return id; }
    public User getCreator() { return creator; }
    public String getSlug() { return slug; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getLicense() { return license; }
    public String getRepositoryUrl() { return repositoryUrl; }
    public TemplateVisibility getVisibility() { return visibility; }
    public TemplateStatus getStatus() { return status; }
    public Template getParentTemplate() { return parentTemplate; }
    public Template getOriginalTemplate() { return originalTemplate; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setCreator(User creator) { this.creator = creator; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setLicense(String license) { this.license = license; }
    public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }
    public void setVisibility(TemplateVisibility visibility) { this.visibility = visibility; }
    public void setStatus(TemplateStatus status) { this.status = status; }
    public void setParentTemplate(Template parentTemplate) { this.parentTemplate = parentTemplate; }
    public void setOriginalTemplate(Template originalTemplate) { this.originalTemplate = originalTemplate; }
}
