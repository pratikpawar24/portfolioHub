package com.portfoliohub.portfolio.dto;
import java.time.Instant; import java.util.UUID; import tools.jackson.databind.JsonNode;
public record RevisionResponse(UUID id,long revisionNumber,String schemaVersion,JsonNode content,Instant createdAt) {}
