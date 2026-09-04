package com.portfoliohub.template.dto;

import com.portfoliohub.template.entity.TemplateStatus;
import com.portfoliohub.template.entity.TemplateVisibility;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TemplateResponse(
        UUID id,
        UUID creatorUserId,
        String slug,
        String name,
        String description,
        String category,
        String license,
        String repositoryUrl,
        TemplateVisibility visibility,
        TemplateStatus status,
        UUID parentTemplateId,
        UUID originalTemplateId,
        Instant createdAt,
        Instant updatedAt,
        List<TemplateVersionResponse> versions
) {}
