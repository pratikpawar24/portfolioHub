package com.portfoliohub.publishing.controller;

import com.portfoliohub.common.api.ApiException;
import com.portfoliohub.portfolio.entity.Portfolio;
import com.portfoliohub.portfolio.entity.PortfolioStatus;
import com.portfoliohub.portfolio.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.*;

@RestController
public class PublishedSiteController {
    private final PortfolioRepository portfolios;
    private final Path publishedRoot;

    public PublishedSiteController(PortfolioRepository portfolios,
                                    @Value("${app.publishing.published-root:${user.dir}/data/publishing/sites}") String publishedRoot) {
        this.portfolios = portfolios;
        this.publishedRoot = Paths.get(publishedRoot).toAbsolutePath().normalize();
    }

    @GetMapping("/p/{slug}")
    public ResponseEntity<Void> siteRoot(@PathVariable String slug) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/").build().toUri());
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(headers).build();
    }

    @GetMapping({"/p/{slug}/", "/p/{slug}/**"})
    public ResponseEntity<Resource> site(
            @PathVariable String slug,
            HttpServletRequest request) {
        Portfolio portfolio = portfolios.findBySlugAndStatus(slug.trim().toLowerCase(), PortfolioStatus.PUBLISHED)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PUBLIC_PORTFOLIO_NOT_FOUND", "Published portfolio was not found"));

        String prefix = request.getRequestURI();
        String base = "/p/" + portfolio.getSlug();
        String relative = prefix.length() <= base.length() ? "" : prefix.substring(base.length());
        if (relative.startsWith("/")) relative = relative.substring(1);
        if (relative.isBlank()) relative = "index.html";

        Path root = publishedRoot.resolve(safeSlug(portfolio.getSlug())).normalize();
        Path requested = root.resolve(relative).normalize();
        if (!requested.startsWith(root)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_PUBLIC_PATH", "Invalid public asset path");
        }

        if (!Files.exists(requested) || Files.isDirectory(requested)) {
            // SPA fallback: serve index.html for browser navigation, but never for obvious file requests.
            String accept = request.getHeader("Accept");
            if (accept != null && accept.contains("text/html") && !relative.contains(".")) {
                requested = root.resolve("index.html").normalize();
            } else {
                throw new ApiException(HttpStatus.NOT_FOUND, "PUBLIC_ASSET_NOT_FOUND", "Published asset was not found");
            }
        }

        FileSystemResource resource = new FileSystemResource(requested);
        MediaType mediaType = mediaType(requested.getFileName().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        if (requested.getFileName().toString().equals("index.html")) {
            headers.setCacheControl(CacheControl.noCache());
        } else {
            headers.setCacheControl(CacheControl.maxAge(java.time.Duration.ofMinutes(5)).cachePublic());
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    private String safeSlug(String slug) {
        String safe = slug.replaceAll("[^a-zA-Z0-9_-]", "-");
        if (!safe.equals(slug)) {
            // The publisher uses the same normalization. This branch mainly makes traversal intent explicit.
            return safe;
        }
        return safe;
    }

    private MediaType mediaType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".html")) return MediaType.TEXT_HTML;
        if (lower.endsWith(".css")) return MediaType.parseMediaType("text/css");
        if (lower.endsWith(".js")) return MediaType.parseMediaType("text/javascript");
        if (lower.endsWith(".json")) return MediaType.APPLICATION_JSON;
        if (lower.endsWith(".svg")) return MediaType.parseMediaType("image/svg+xml");
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".webp")) return MediaType.parseMediaType("image/webp");
        if (lower.endsWith(".ico")) return MediaType.parseMediaType("image/x-icon");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
