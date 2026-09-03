package com.portfoliohub.portfolios.api;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public final class PortfolioDtos {
    private PortfolioDtos() {}

    public record CreatePortfolioRequest(
            @NotBlank @Size(max = 80) String title,
            @NotBlank @Size(max = 80) @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*") String slug,
            JsonNode content
    ) {}

    public record UpdatePortfolioRequest(
            @NotBlank @Size(max = 80) String title,
            @NotBlank @Size(max = 80) @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*") String slug,
            JsonNode content,
            Long expectedRevisionNumber
    ) {}

    public record PortfolioSummaryResponse(
            UUID id,
            String title,
            String slug,
            String status,
            long version,
            long revisionNumber,
            Instant updatedAt
    ) {}

    public record PortfolioResponse(
            UUID id,
            UUID ownerUserId,
            String title,
            String slug,
            String status,
            UUID activeTemplateVersionId,
            UUID currentDraftRevisionId,
            UUID publishedRevisionId,
            Instant publishedAt,
            long version,
            long revisionNumber,
            String schemaVersion,
            JsonNode content,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record PublicPortfolioResponse(
            UUID portfolioId,
            String username,
            String title,
            String slug,
            String schemaVersion,
            JsonNode content,
            UUID templateVersionId,
            Instant publishedAt
    ) {}
}
