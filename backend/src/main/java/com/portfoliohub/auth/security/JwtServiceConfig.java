package com.portfoliohub.auth.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class JwtServiceConfig {
    @Bean
    JwtService jwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.issuer}") String issuer,
            @Value("${app.security.jwt.access-token-ttl:PT15M}") Duration accessTokenTtl) {
        return new JwtService(secret, issuer, accessTokenTtl);
    }
}
