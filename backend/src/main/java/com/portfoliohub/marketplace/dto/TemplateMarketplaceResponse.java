package com.portfoliohub.marketplace.dto;

import com.portfoliohub.template.entity.TemplateStatus;
import com.portfoliohub.template.entity.TemplateVisibility;

import java.time.Instant;
import java.util.UUID;
import java.util.List;
import com.portfoliohub.template.dto.TemplateVersionResponse;

public record TemplateMarketplaceResponse(
        UUID id,
        String slug,
        String name,
        String description,
        String category,
        String framework,
        String license,
        String repositoryUrl,
        TemplateVisibility visibility,
        TemplateStatus status,
        UUID creatorUserId,
        String creatorUsername,
        String creatorDisplayName,
        UUID parentTemplateId,
        UUID originalTemplateId,
        Instant createdAt,
        Instant updatedAt,
        List<TemplateVersionResponse> versions,
        TemplateMarketplaceStatsResponse stats
) {}
