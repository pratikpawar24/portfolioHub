package com.portfoliohub.template.dto;

import com.portfoliohub.template.entity.TemplateStatus;
import com.portfoliohub.template.entity.TemplateVisibility;

import java.time.Instant;
import java.util.UUID;

public record TemplateSummaryResponse(
        UUID id,
        String slug,
        String name,
        String description,
        String category,
        String license,
        TemplateVisibility visibility,
        TemplateStatus status,
        Instant updatedAt
) {}
