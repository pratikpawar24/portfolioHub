package com.portfoliohub.creator.dto;

import jakarta.validation.constraints.Size;

public record CreatorProfileRequest(
        @Size(max = 1000) String bio,
        @Size(max = 500) String avatarUrl,
        @Size(max = 500) String websiteUrl
) {}
