package com.portfoliohub.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;

@Configuration
public class AuthConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean(name = "refreshTokenTtl")
    Duration refreshTokenTtl(@Value("${app.security.jwt.refresh-token-ttl:P30D}") Duration ttl) {
        return ttl;
    }

    @Bean(name = "accessTokenTtl")
    Duration accessTokenTtl(@Value("${app.security.jwt.access-token-ttl:PT15M}") Duration ttl) {
        return ttl;
    }
}
