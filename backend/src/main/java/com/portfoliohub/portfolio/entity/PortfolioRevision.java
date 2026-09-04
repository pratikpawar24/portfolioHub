package com.portfoliohub.portfolio.entity;

import com.portfoliohub.auth.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "portfolio_revisions", uniqueConstraints = @UniqueConstraint(name = "uk_portfolio_revisions_number", columnNames = {"portfolio_id", "revision_number"}))
public class PortfolioRevision {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "portfolio_id", nullable = false) private Portfolio portfolio;
    @Column(name = "revision_number", nullable = false) private long revisionNumber;
    @Column(name = "schema_version", nullable = false, length = 32) private String schemaVersion;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb", nullable = false) private JsonNode content;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "created_by", nullable = false) private User createdBy;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void onCreate(){ createdAt = Instant.now(); }
    public UUID getId(){return id;} public Portfolio getPortfolio(){return portfolio;} public long getRevisionNumber(){return revisionNumber;}
    public String getSchemaVersion(){return schemaVersion;} public JsonNode getContent(){return content;} public User getCreatedBy(){return createdBy;} public Instant getCreatedAt(){return createdAt;}
    public void setPortfolio(Portfolio v){portfolio=v;} public void setRevisionNumber(long v){revisionNumber=v;} public void setSchemaVersion(String v){schemaVersion=v;} public void setContent(JsonNode v){content=v;} public void setCreatedBy(User v){createdBy=v;}
}
