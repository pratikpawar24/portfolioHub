package com.portfoliohub.marketplace.dto;

public record TemplateMarketplaceStatsResponse(
        long likeCount,
        long favoriteCount,
        long usageCount,
        long forkCount,
        long remixCount,
        long popularityScore,
        boolean likedByCurrentUser,
        boolean favoritedByCurrentUser
) {}
