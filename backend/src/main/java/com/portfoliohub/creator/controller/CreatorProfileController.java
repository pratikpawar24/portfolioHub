package com.portfoliohub.creator.controller;

import com.portfoliohub.creator.dto.CreatorProfileRequest;
import com.portfoliohub.creator.dto.CreatorProfileResponse;
import com.portfoliohub.creator.service.CreatorProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CreatorProfileController {
    private final CreatorProfileService service;

    public CreatorProfileController(CreatorProfileService service) {
        this.service = service;
    }

    @PutMapping("/creator-profile")
    public CreatorProfileResponse upsert(@AuthenticationPrincipal Jwt jwt,
                                         @Valid @RequestBody CreatorProfileRequest request) {
        return service.upsert(UUID.fromString(jwt.getSubject()), request);
    }

    @GetMapping("/creators/{username}")
    public CreatorProfileResponse get(@PathVariable String username) {
        return service.getByUsername(username);
    }
}
