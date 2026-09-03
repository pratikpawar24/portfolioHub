package com.portfoliohub.portfolios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.portfoliohub.portfolios.api.PortfolioDtos;
import com.portfoliohub.portfolios.service.PortfolioService;
import com.portfoliohub.users.domain.User;
import com.portfoliohub.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class PortfolioIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("portfoliohub_test")
            .withUsername("portfoliohub_test")
            .withPassword("portfoliohub_test");

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:8-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired PortfolioService portfolioService;
    @Autowired ObjectMapper objectMapper;

    private UUID userId;
    private String username;

    @BeforeEach
    void createUser() {
        username = "b2" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        User user = userRepository.save(new User(
                "b2-test-" + UUID.randomUUID() + "@example.com",
                username,
                "B2 Tester",
                passwordEncoder.encode("Password123!")));
        userId = user.getId();
    }

    @Test
    void createsEditsPublishesAndServesPublicPortfolio() {
        ObjectNode content = objectMapper.createObjectNode()
                .put("schemaVersion", "1.0")
                .set("profile", objectMapper.createObjectNode().put("displayName", "B2 Tester"));

        PortfolioDtos.PortfolioResponse created = portfolioService.create(userId,
                new PortfolioDtos.CreatePortfolioRequest("Test Portfolio", "test-portfolio", content));
        assertEquals("DRAFT", created.status());
        assertEquals(1, created.revisionNumber());

        PortfolioDtos.PortfolioResponse edited = portfolioService.update(userId, created.id(),
                new PortfolioDtos.UpdatePortfolioRequest("Updated Portfolio", "updated-portfolio", content, 1L));
        assertEquals(2, edited.revisionNumber());

        PortfolioDtos.PortfolioResponse published = portfolioService.publish(userId, created.id());
        assertEquals("PUBLISHED", published.status());
        assertNotNull(published.publishedAt());

        PortfolioDtos.PublicPortfolioResponse publicPortfolio =
                portfolioService.getPublic(username, "updated-portfolio");
        assertEquals(created.id(), publicPortfolio.portfolioId());
        assertEquals("Updated Portfolio", publicPortfolio.title());
        assertEquals("1.0", publicPortfolio.schemaVersion());

        PortfolioDtos.PortfolioResponse unpublished = portfolioService.unpublish(userId, created.id());
        assertEquals("UNPUBLISHED", unpublished.status());

        PortfolioDtos.PortfolioResponse editedWhileUnpublished = portfolioService.update(userId, created.id(),
                new PortfolioDtos.UpdatePortfolioRequest("Updated Again", "updated-again", content, 2L));
        assertEquals("UNPUBLISHED", editedWhileUnpublished.status());

        assertThrows(RuntimeException.class, () -> portfolioService.getPublic(username, "updated-again"));
    }
}
