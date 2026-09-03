package com.portfoliohub.portfolios.api;

import com.portfoliohub.auth.security.AuthenticatedUser;
import com.portfoliohub.portfolios.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/portfolios")
public class PortfolioController {
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    public ResponseEntity<PortfolioDtos.PortfolioResponse> create(
            @AuthenticatedUser UUID userId,
            @Valid @RequestBody PortfolioDtos.CreatePortfolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.create(userId, request));
    }

    @GetMapping
    public List<PortfolioDtos.PortfolioSummaryResponse> listMine(@AuthenticatedUser UUID userId) {
        return portfolioService.listMine(userId);
    }

    @GetMapping("/{portfolioId}")
    public PortfolioDtos.PortfolioResponse getMine(
            @AuthenticatedUser UUID userId,
            @PathVariable UUID portfolioId) {
        return portfolioService.getMine(userId, portfolioId);
    }

    @PutMapping("/{portfolioId}")
    public PortfolioDtos.PortfolioResponse update(
            @AuthenticatedUser UUID userId,
            @PathVariable UUID portfolioId,
            @Valid @RequestBody PortfolioDtos.UpdatePortfolioRequest request) {
        return portfolioService.update(userId, portfolioId, request);
    }

    @PostMapping("/{portfolioId}/publish")
    public PortfolioDtos.PortfolioResponse publish(
            @AuthenticatedUser UUID userId,
            @PathVariable UUID portfolioId) {
        return portfolioService.publish(userId, portfolioId);
    }

    @PostMapping("/{portfolioId}/unpublish")
    public PortfolioDtos.PortfolioResponse unpublish(
            @AuthenticatedUser UUID userId,
            @PathVariable UUID portfolioId) {
        return portfolioService.unpublish(userId, portfolioId);
    }

    @DeleteMapping("/{portfolioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(
            @AuthenticatedUser UUID userId,
            @PathVariable UUID portfolioId) {
        portfolioService.archive(userId, portfolioId);
    }
}
