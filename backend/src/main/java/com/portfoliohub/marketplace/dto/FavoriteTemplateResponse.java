package com.portfoliohub.marketplace.dto;

import java.time.Instant;
import java.util.UUID;

public record FavoriteTemplateResponse(
        UUID templateId,
        String templateSlug,
        String templateName,
        Instant favoritedAt
) {}
