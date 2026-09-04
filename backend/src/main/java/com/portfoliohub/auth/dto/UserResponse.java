package com.portfoliohub.auth.dto;

import com.portfoliohub.auth.entity.User;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String username,
        String displayName,
        String role,
        String status,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getDisplayName(),
                user.getRole().name(), user.getStatus().name(), user.getCreatedAt());
    }
}
