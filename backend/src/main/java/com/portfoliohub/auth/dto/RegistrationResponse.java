package com.portfoliohub.auth.dto;

public record RegistrationResponse(
        UserResponse user,
        boolean verificationRequired,
        String message
) {}
