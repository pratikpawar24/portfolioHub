package com.portfoliohub.marketplace.controller;

import com.portfoliohub.marketplace.dto.FavoriteTemplateResponse;
import com.portfoliohub.marketplace.service.MarketplaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/me/template-favorites")
public class TemplateFavoriteController {
    private final MarketplaceService service;

    public TemplateFavoriteController(MarketplaceService service) {
        this.service = service;
    }

    @GetMapping
    public Page<FavoriteTemplateResponse> list(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.favorites(UUID.fromString(jwt.getSubject()), pageable);
    }
}
