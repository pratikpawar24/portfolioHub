package com.portfoliohub.creator.dto;

import java.time.Instant;
import java.util.UUID;

public record CreatorProfileResponse(
        UUID userId,
        String username,
        String displayName,
        String bio,
        String avatarUrl,
        String websiteUrl,
        long templateCount,
        Instant updatedAt
) {}
