package com.portfoliohub.marketplace.controller;

import com.portfoliohub.marketplace.service.MarketplaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplateReactionController {
    private final MarketplaceService service;

    public TemplateReactionController(MarketplaceService service) {
        this.service = service;
    }

    @PutMapping("/{templateId}/like")
    public ResponseEntity<Void> like(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID templateId) {
        service.like(userId(jwt), templateId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{templateId}/like")
    public ResponseEntity<Void> unlike(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID templateId) {
        service.unlike(userId(jwt), templateId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{templateId}/favorite")
    public ResponseEntity<Void> favorite(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID templateId) {
        service.favorite(userId(jwt), templateId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{templateId}/favorite")
    public ResponseEntity<Void> unfavorite(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID templateId) {
        service.unfavorite(userId(jwt), templateId);
        return ResponseEntity.noContent().build();
    }

    private UUID userId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
