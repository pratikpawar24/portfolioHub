package com.portfoliohub.auth.service;

import com.portfoliohub.support.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest extends PostgresIntegrationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void passwordEncoderDoesNotStoreRawPassword() {
        String raw = "PortfolioHub!2026";
        String encoded = passwordEncoder.encode(raw);

        assertThat(encoded).isNotEqualTo(raw);
        assertThat(passwordEncoder.matches(raw, encoded)).isTrue();
    }
}
