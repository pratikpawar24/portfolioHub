package com.portfoliohub.template.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "template_versions", uniqueConstraints = @UniqueConstraint(
        name = "uk_template_versions_template_version",
        columnNames = {"template_id", "version"}))
public class TemplateVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false, length = 30)
    private String version;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode manifest;

    @Column(name = "schema_min", nullable = false, length = 32)
    private String schemaMin;

    @Column(name = "schema_max", nullable = false, length = 32)
    private String schemaMax;

    @Column(name = "source_reference", length = 500)
    private String sourceReference;

    @Column(name = "artifact_reference", length = 500)
    private String artifactReference;

    @Column(name = "preview_reference", length = 500)
    private String previewReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TemplateVersionStatus status = TemplateVersionStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        if (version != null) version = version.trim();
        if (schemaMin != null) schemaMin = schemaMin.trim();
        if (schemaMax != null) schemaMax = schemaMax.trim();
    }

    public UUID getId() { return id; }
    public Template getTemplate() { return template; }
    public String getVersion() { return version; }
    public JsonNode getManifest() { return manifest; }
    public String getSchemaMin() { return schemaMin; }
    public String getSchemaMax() { return schemaMax; }
    public String getSourceReference() { return sourceReference; }
    public String getArtifactReference() { return artifactReference; }
    public String getPreviewReference() { return previewReference; }
    public TemplateVersionStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void setTemplate(Template template) { this.template = template; }
    public void setVersion(String version) { this.version = version; }
    public void setManifest(JsonNode manifest) { this.manifest = manifest; }
    public void setSchemaMin(String schemaMin) { this.schemaMin = schemaMin; }
    public void setSchemaMax(String schemaMax) { this.schemaMax = schemaMax; }
    public void setSourceReference(String sourceReference) { this.sourceReference = sourceReference; }
    public void setArtifactReference(String artifactReference) { this.artifactReference = artifactReference; }
    public void setPreviewReference(String previewReference) { this.previewReference = previewReference; }
    public void setStatus(TemplateVersionStatus status) { this.status = status; }
}
