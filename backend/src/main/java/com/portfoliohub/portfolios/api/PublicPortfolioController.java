package com.portfoliohub.portfolios.api;

import com.portfoliohub.portfolios.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/portfolios")
public class PublicPortfolioController {
    private final PortfolioService portfolioService;

    public PublicPortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/{username}/{slug}")
    public PortfolioDtos.PublicPortfolioResponse getPublic(
            @PathVariable String username,
            @PathVariable String slug) {
        return portfolioService.getPublic(username, slug);
    }
}
