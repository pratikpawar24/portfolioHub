package com.portfoliohub.publishing.controller;

import com.portfoliohub.publishing.dto.PublishJobResponse;
import com.portfoliohub.publishing.service.PublishingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/portfolios")
public class PublishingController {
    private final PublishingService publishingService;

    public PublishingController(PublishingService publishingService) {
        this.publishingService = publishingService;
    }

    @PostMapping("/{portfolioId}/publish")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PublishJobResponse publish(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID portfolioId) {
        return publishingService.requestPublish(UUID.fromString(jwt.getSubject()), portfolioId);
    }

    @PostMapping("/{portfolioId}/unpublish")
    public void unpublish(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID portfolioId) {
        publishingService.unpublish(UUID.fromString(jwt.getSubject()), portfolioId);
    }

    @GetMapping("/{portfolioId}/publish/jobs")
    public Page<PublishJobResponse> jobs(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID portfolioId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return publishingService.listJobs(UUID.fromString(jwt.getSubject()), portfolioId, pageable);
    }

    @GetMapping("/publish/jobs/{jobId}")
    public PublishJobResponse job(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID jobId) {
        return publishingService.getJob(UUID.fromString(jwt.getSubject()), jobId);
    }
}
