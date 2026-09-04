package com.portfoliohub.template.controller;

import com.portfoliohub.marketplace.dto.TemplateMarketplaceResponse;
import com.portfoliohub.marketplace.service.MarketplaceService;
import com.portfoliohub.template.dto.TemplateVersionResponse;
import com.portfoliohub.template.service.TemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {
    private final TemplateService service;
    private final MarketplaceService marketplace;

    public TemplateController(TemplateService service, MarketplaceService marketplace) {
        this.service = service;
        this.marketplace = marketplace;
    }

    @GetMapping
    public Page<TemplateMarketplaceResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String framework,
            @RequestParam(defaultValue = "newest") String sort,
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return marketplace.search(q, category, framework, sort, pageable, userId(jwt));
    }

    @GetMapping("/{slug}")
    public TemplateMarketplaceResponse get(
            @PathVariable String slug,
            @AuthenticationPrincipal Jwt jwt) {
        return marketplace.get(slug, userId(jwt));
    }

    @GetMapping("/versions/{templateVersionId}")
    public TemplateVersionResponse getVersion(@PathVariable UUID templateVersionId) {
        return service.getApprovedVersion(templateVersionId);
    }

    private UUID userId(Jwt jwt) {
        return jwt == null ? null : UUID.fromString(jwt.getSubject());
    }
}
