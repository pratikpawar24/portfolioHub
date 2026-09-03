package com.portfoliohub.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank @Email @Size(max = 320) String email,
            @NotBlank @Pattern(regexp = "^[a-z0-9](?:[a-z0-9_-]{1,28}[a-z0-9])?$", message = "username must be 3-30 characters and use lowercase letters, numbers, underscore or hyphen") String username,
            @NotBlank @Size(min = 2, max = 120) String displayName,
            @NotBlank @Size(min = 12, max = 72) String password
    ) {}

    public record LoginRequest(
            @NotBlank @Email @Size(max = 320) String email,
            @NotBlank @Size(min = 1, max = 72) String password
    ) {}

    public record RefreshRequest(@NotBlank String refreshToken) {}

    public record LogoutRequest(@NotBlank String refreshToken) {}

    public record UserResponse(
            UUID id,
            String email,
            String username,
            String displayName,
            String role,
            String status,
            boolean emailVerified,
            Instant createdAt
    ) {}

    public record AuthResponse(
            String tokenType,
            String accessToken,
            long expiresInSeconds,
            String refreshToken,
            UserResponse user
    ) {}
}
