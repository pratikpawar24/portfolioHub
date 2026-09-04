package com.portfoliohub.template.controller;

import com.portfoliohub.template.dto.SelectTemplateRequest;
import com.portfoliohub.template.dto.TemplateVersionResponse;
import com.portfoliohub.template.entity.TemplateVersion;
import com.portfoliohub.template.service.TemplateService;
import com.portfoliohub.marketplace.service.MarketplaceService;
import com.portfoliohub.portfolio.entity.Portfolio;
import com.portfoliohub.portfolio.entity.PortfolioStatus;
import com.portfoliohub.portfolio.repository.PortfolioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.portfoliohub.common.api.ApiException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/portfolios")
public class PortfolioTemplateController {
    private final PortfolioRepository portfolios;
    private final TemplateService templates;
    private final MarketplaceService marketplace;

    public PortfolioTemplateController(PortfolioRepository portfolios, TemplateService templates, MarketplaceService marketplace) {
        this.portfolios = portfolios;
        this.templates = templates;
        this.marketplace = marketplace;
    }

    @PutMapping("/{portfolioId}/template")
    @Transactional
    public TemplateVersionResponse select(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID portfolioId,
            @Valid @RequestBody SelectTemplateRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Portfolio portfolio = portfolios.findByOwnerIdAndId(userId, portfolioId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "Portfolio was not found"));
        if (portfolio.getStatus() == PortfolioStatus.ARCHIVED) {
            throw new ApiException(HttpStatus.CONFLICT, "PORTFOLIO_ARCHIVED", "Archived portfolios cannot change templates");
        }
        String schemaVersion = portfolio.getCurrentDraftRevision() == null
                ? null
                : portfolio.getCurrentDraftRevision().getSchemaVersion();
        if (schemaVersion == null) {
            throw new ApiException(HttpStatus.CONFLICT, "NO_PORTFOLIO_SCHEMA", "Portfolio has no draft schema version");
        }
        TemplateVersion version = templates.requireCompatibleApprovedVersion(request.templateVersionId(), schemaVersion);
        UUID previousVersionId = portfolio.getActiveTemplateVersionId();
        if (!version.getId().equals(previousVersionId)) {
            portfolio.setActiveTemplateVersionId(version.getId());
            portfolios.save(portfolio);
            if (previousVersionId != null) {
                marketplace.refreshUsageForTemplateVersion(previousVersionId);
            }
            marketplace.refreshUsageForTemplateVersion(version.getId());
        }
        return templates.getApprovedVersion(version.getId());
    }

    @DeleteMapping("/{portfolioId}/template")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void clear(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID portfolioId) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Portfolio portfolio = portfolios.findByOwnerIdAndId(userId, portfolioId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "Portfolio was not found"));
        if (portfolio.getStatus() == PortfolioStatus.ARCHIVED) {
            throw new ApiException(HttpStatus.CONFLICT, "PORTFOLIO_ARCHIVED", "Archived portfolios cannot change templates");
        }
        UUID previousVersionId = portfolio.getActiveTemplateVersionId();
        portfolio.setActiveTemplateVersionId(null);
        portfolios.save(portfolio);
        if (previousVersionId != null) {
            marketplace.refreshUsageForTemplateVersion(previousVersionId);
        }
    }
}
