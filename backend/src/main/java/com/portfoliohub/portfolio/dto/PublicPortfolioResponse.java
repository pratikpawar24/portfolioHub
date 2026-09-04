package com.portfoliohub.portfolio.dto;
import java.time.Instant; import java.util.UUID; import tools.jackson.databind.JsonNode;
public record PublicPortfolioResponse(UUID id,String slug,String title,String schemaVersion,JsonNode content,UUID templateVersionId,Instant publishedAt) {}
