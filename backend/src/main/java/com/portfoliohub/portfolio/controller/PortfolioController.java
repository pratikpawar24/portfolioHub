package com.portfoliohub.portfolio.controller;

import com.portfoliohub.portfolio.dto.CreatePortfolioRequest;
import com.portfoliohub.portfolio.dto.PortfolioResponse;
import com.portfoliohub.portfolio.dto.PortfolioSummaryResponse;
import com.portfoliohub.portfolio.dto.UpdatePortfolioRequest;
import com.portfoliohub.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
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
public class PortfolioController {
    private final PortfolioService service;

    public PortfolioController(PortfolioService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortfolioResponse create(@AuthenticationPrincipal Jwt jwt,
                                    @Valid @RequestBody CreatePortfolioRequest request) {
        return service.create(UUID.fromString(jwt.getSubject()), request);
    }

    @GetMapping
    public Page<PortfolioSummaryResponse> list(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return service.list(UUID.fromString(jwt.getSubject()), pageable);
    }

    @GetMapping("/{id}")
    public PortfolioResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return service.get(UUID.fromString(jwt.getSubject()), id);
    }

    @PutMapping("/{id}")
    public PortfolioResponse update(@AuthenticationPrincipal Jwt jwt,
                                    @PathVariable UUID id,
                                    @Valid @RequestBody UpdatePortfolioRequest request) {
        return service.update(UUID.fromString(jwt.getSubject()), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        service.archive(UUID.fromString(jwt.getSubject()), id);
    }
}
