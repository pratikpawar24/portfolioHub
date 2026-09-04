package com.portfoliohub.portfolio.dto;
import jakarta.validation.constraints.*; import tools.jackson.databind.JsonNode;
public record UpdatePortfolioRequest(@NotBlank @Size(max=120) String title,@NotBlank @Size(max=32) String schemaVersion,@NotNull JsonNode content) {}
