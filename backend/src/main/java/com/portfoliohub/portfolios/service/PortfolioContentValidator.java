package com.portfoliohub.portfolios.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.portfoliohub.common.api.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PortfolioContentValidator {
    public static final String CURRENT_SCHEMA_VERSION = "1.0";
    private static final Set<String> TOP_LEVEL_FIELDS = Set.of(
            "schemaVersion", "profile", "links", "skills", "projects", "experience",
            "education", "certifications", "achievements", "services", "testimonials", "customSections"
    );

    public JsonNode validate(JsonNode content) {
        if (content == null || !content.isObject()) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_PORTFOLIO_CONTENT", "Portfolio content must be a JSON object");
        }

        JsonNode schemaVersion = content.get("schemaVersion");
        if (schemaVersion == null || !schemaVersion.isTextual()) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_SCHEMA_VERSION", "schemaVersion is required");
        }
        if (!CURRENT_SCHEMA_VERSION.equals(schemaVersion.asText())) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "UNSUPPORTED_SCHEMA_VERSION", "Unsupported portfolio schema version");
        }

        for (String field : content.fieldNames()) {
            if (!TOP_LEVEL_FIELDS.contains(field)) {
                throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "UNKNOWN_PORTFOLIO_FIELD", "Unknown top-level portfolio field: " + field);
            }
        }

        return content;
    }
}
