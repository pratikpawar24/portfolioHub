package com.portfoliohub.template.dto;

import com.portfoliohub.template.entity.TemplateVersionStatus;
import tools.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record TemplateVersionResponse(
        UUID id,
        UUID templateId,
        String version,
        JsonNode manifest,
        String schemaMin,
        String schemaMax,
        String sourceReference,
        String artifactReference,
        String previewReference,
        TemplateVersionStatus status,
        Instant createdAt
) {}
