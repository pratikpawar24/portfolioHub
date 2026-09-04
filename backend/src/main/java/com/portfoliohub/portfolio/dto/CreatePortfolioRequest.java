package com.portfoliohub.portfolio.dto;
import jakarta.validation.constraints.*; import tools.jackson.databind.JsonNode;
public record CreatePortfolioRequest(@NotBlank @Size(max=120) String title,@NotBlank @Pattern(regexp="[a-z0-9](?:[a-z0-9-]{1,78}[a-z0-9])?",message="slug must contain lowercase letters, numbers and hyphens") String slug,@NotBlank @Size(max=32) String schemaVersion,@NotNull JsonNode content) {}
