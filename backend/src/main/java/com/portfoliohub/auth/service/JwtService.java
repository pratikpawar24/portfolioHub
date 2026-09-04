package com.portfoliohub.auth.service;

import com.portfoliohub.auth.security.UserPrincipal;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    private final String issuer;
    private final long accessTokenSeconds;

    public JwtService(JwtEncoder encoder,
                      @Value("${app.security.jwt.issuer:portfoliohub}") String issuer,
                      @Value("${app.security.jwt.access-token-seconds:900}") long accessTokenSeconds) {
        this.encoder = encoder;
        this.issuer = issuer;
        this.accessTokenSeconds = accessTokenSeconds;
    }

    public long accessTokenSeconds() { return accessTokenSeconds; }

    public String createAccessToken(UserPrincipal principal) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(principal.id().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenSeconds))
                .id(UUID.randomUUID().toString())
                .claim("email", principal.email())
                .claim("username", principal.username())
                .claim("role", principal.role())
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Configuration
    static class JwtConfiguration {
        @Bean
        SecretKey jwtSecretKey(@Value("${app.security.jwt.secret}") String secret) {
            byte[] key = secret.getBytes(StandardCharsets.UTF_8);
            if (key.length < 32) throw new IllegalStateException("app.security.jwt.secret must be at least 32 bytes");
            return new SecretKeySpec(key, "HmacSHA256");
        }

        @Bean
        JwtEncoder jwtEncoder(SecretKey key) {
            return new NimbusJwtEncoder(new ImmutableSecret<>(key));
        }

        @Bean
        JwtDecoder jwtDecoder(SecretKey key, @Value("${app.security.jwt.issuer:portfoliohub}") String issuer) {
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).build();
            decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
            return decoder;
        }
    }
}
