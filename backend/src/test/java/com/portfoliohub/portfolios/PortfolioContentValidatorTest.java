package com.portfoliohub.portfolios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.portfoliohub.common.api.ApiException;
import com.portfoliohub.portfolios.service.PortfolioContentValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioContentValidatorTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final PortfolioContentValidator validator = new PortfolioContentValidator();

    @Test
    void acceptsCurrentSchema() {
        ObjectNode content = mapper.createObjectNode().put("schemaVersion", "1.0");
        assertDoesNotThrow(() -> validator.validate(content));
    }

    @Test
    void rejectsMissingSchemaVersion() {
        ObjectNode content = mapper.createObjectNode();
        ApiException ex = assertThrows(ApiException.class, () -> validator.validate(content));
        assertEquals("INVALID_SCHEMA_VERSION", ex.code());
    }

    @Test
    void rejectsUnknownTopLevelField() {
        ObjectNode content = mapper.createObjectNode().put("schemaVersion", "1.0").put("unknown", true);
        ApiException ex = assertThrows(ApiException.class, () -> validator.validate(content));
        assertEquals("UNKNOWN_PORTFOLIO_FIELD", ex.code());
    }
}
