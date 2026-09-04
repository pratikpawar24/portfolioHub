package com.portfoliohub.template.dto;

import com.portfoliohub.template.entity.TemplateVisibility;
import jakarta.validation.constraints.*;
import tools.jackson.databind.JsonNode;

public record RegisterTemplateRequest(
        @NotBlank @Pattern(regexp = "[a-z0-9](?:[a-z0-9-]{1,98}[a-z0-9])?", message = "slug must contain lowercase letters, numbers and hyphens") String slug,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 2000) String description,
        @Size(max = 60) String category,
        @NotBlank @Size(max = 100) String license,
        @Size(max = 500) String repositoryUrl,
        @NotNull TemplateVisibility visibility,
        @NotBlank @Pattern(regexp = "\\d+\\.\\d+\\.\\d+", message = "version must use semantic version x.y.z") String version,
        @NotNull JsonNode manifest,
        @NotBlank @Size(max = 32) String schemaMin,
        @NotBlank @Size(max = 32) String schemaMax,
        @Size(max = 500) String sourceReference,
        @Size(max = 500) String artifactReference,
        @Size(max = 500) String previewReference
) {}
