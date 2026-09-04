package com.portfoliohub.marketplace.entity;

import com.portfoliohub.auth.entity.User;
import com.portfoliohub.template.entity.Template;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "template_likes", uniqueConstraints = @UniqueConstraint(name = "uk_template_likes_user_template", columnNames = {"user_id", "template_id"}))
public class TemplateLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public Template getTemplate() { return template; }
    public Instant getCreatedAt() { return createdAt; }
    public void setUser(User user) { this.user = user; }
    public void setTemplate(Template template) { this.template = template; }
}
