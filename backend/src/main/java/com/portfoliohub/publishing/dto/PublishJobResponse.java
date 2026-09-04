package com.portfoliohub.publishing.dto;

import com.portfoliohub.publishing.entity.PublishJobStatus;

import java.time.Instant;
import java.util.UUID;

public record PublishJobResponse(
        UUID id,
        UUID portfolioId,
        UUID revisionId,
        UUID templateVersionId,
        PublishJobStatus status,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt,
        String publicUrl,
        String errorMessage) {
}
