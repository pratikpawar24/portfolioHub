package com.portfoliohub.portfolio.controller;
import com.portfoliohub.portfolio.dto.PublicPortfolioResponse; import com.portfoliohub.portfolio.service.PortfolioService; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/public/portfolios") public class PublicPortfolioController { private final PortfolioService service; public PublicPortfolioController(PortfolioService service){this.service=service;} @GetMapping("/{slug}") public PublicPortfolioResponse get(@PathVariable String slug){return service.publicPortfolio(slug);} }
