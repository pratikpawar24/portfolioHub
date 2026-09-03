package com.portfoliohub.portfolios.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "portfolio_revisions", indexes = {
        @Index(name = "idx_portfolio_revisions_portfolio_revision", columnList = "portfolio_id, revision_number DESC"),
        @Index(name = "idx_portfolio_revisions_created_by", columnList = "created_by")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_portfolio_revisions_number", columnNames = {"portfolio_id", "revision_number"})
})
public class PortfolioRevision {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "portfolio_id", nullable = false)
    private UUID portfolioId;

    @Column(name = "revision_number", nullable = false)
    private long revisionNumber;

    @Column(name = "schema_version", nullable = false, length = 20)
    private String schemaVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode content;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected PortfolioRevision() {}

    public PortfolioRevision(UUID portfolioId, long revisionNumber, String schemaVersion, JsonNode content, UUID createdBy) {
        this.portfolioId = portfolioId;
        this.revisionNumber = revisionNumber;
        this.schemaVersion = schemaVersion;
        this.content = content.deepCopy();
        this.createdBy = createdBy;
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getPortfolioId() { return portfolioId; }
    public long getRevisionNumber() { return revisionNumber; }
    public String getSchemaVersion() { return schemaVersion; }
    public JsonNode getContent() { return content; }
    public UUID getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
}
