package com.portfoliohub.portfolios.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.portfoliohub.common.api.ApiException;
import com.portfoliohub.portfolios.api.PortfolioDtos;
import com.portfoliohub.portfolios.domain.Portfolio;
import com.portfoliohub.portfolios.domain.PortfolioRevision;
import com.portfoliohub.portfolios.domain.PortfolioStatus;
import com.portfoliohub.portfolios.repository.PortfolioRepository;
import com.portfoliohub.portfolios.repository.PortfolioRevisionRepository;
import com.portfoliohub.users.domain.User;
import com.portfoliohub.users.repository.UserRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioRevisionRepository revisionRepository;
    private final UserRepository userRepository;
    private final PortfolioContentValidator contentValidator;

    public PortfolioService(PortfolioRepository portfolioRepository,
                            PortfolioRevisionRepository revisionRepository,
                            UserRepository userRepository,
                            PortfolioContentValidator contentValidator) {
        this.portfolioRepository = portfolioRepository;
        this.revisionRepository = revisionRepository;
        this.userRepository = userRepository;
        this.contentValidator = contentValidator;
    }

    @Transactional
    public PortfolioDtos.PortfolioResponse create(UUID userId, PortfolioDtos.CreatePortfolioRequest request) {
        String title = request.title().trim();
        String slug = normalizeSlug(request.slug());
        ensureSlugAvailable(userId, slug, null);
        JsonNode content = request.content() == null ? defaultContent(title) : contentValidator.validate(request.content());

        Portfolio portfolio;
        try {
            portfolio = portfolioRepository.saveAndFlush(new Portfolio(userId, title, slug));
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(HttpStatus.CONFLICT, "SLUG_ALREADY_EXISTS", "This portfolio slug is already in use");
        }
        PortfolioRevision revision = revisionRepository.save(new PortfolioRevision(
                portfolio.getId(), 1, PortfolioContentValidator.CURRENT_SCHEMA_VERSION, content, userId));
        portfolio.setCurrentDraftRevisionId(revision.getId());
        portfolioRepository.save(portfolio);
        return toResponse(portfolio, revision);
    }

    @Transactional(readOnly = true)
    public List<PortfolioDtos.PortfolioSummaryResponse> listMine(UUID userId) {
        return portfolioRepository.findAllByOwnerUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public PortfolioDtos.PortfolioResponse getMine(UUID userId, UUID portfolioId) {
        Portfolio portfolio = ownedPortfolio(userId, portfolioId);
        PortfolioRevision revision = currentRevision(portfolio);
        return toResponse(portfolio, revision);
    }

    @Transactional
    public PortfolioDtos.PortfolioResponse update(UUID userId, UUID portfolioId, PortfolioDtos.UpdatePortfolioRequest request) {
        Portfolio portfolio = ownedPortfolio(userId, portfolioId);
        if (portfolio.getStatus() == PortfolioStatus.ARCHIVED) {
            throw new ApiException(HttpStatus.CONFLICT, "PORTFOLIO_ARCHIVED", "Archived portfolios cannot be edited");
        }

        String slug = normalizeSlug(request.slug());
        ensureSlugAvailable(userId, slug, portfolioId);
        PortfolioRevision previous = currentRevision(portfolio);
        long expected = request.expectedRevisionNumber() == null ? previous.getRevisionNumber() : request.expectedRevisionNumber();
        if (expected != previous.getRevisionNumber()) {
            throw new ApiException(HttpStatus.CONFLICT, "REVISION_CONFLICT", "Portfolio has changed since you last loaded it");
        }

        JsonNode content = contentValidator.validate(request.content());
        PortfolioRevision revision = revisionRepository.save(new PortfolioRevision(
                portfolioId, previous.getRevisionNumber() + 1,
                PortfolioContentValidator.CURRENT_SCHEMA_VERSION, content, userId));
        portfolio.updateMetadata(request.title().trim(), slug);
        portfolio.setCurrentDraftRevisionId(revision.getId());
        try {
            portfolioRepository.saveAndFlush(portfolio);
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(HttpStatus.CONFLICT, "SLUG_ALREADY_EXISTS", "This portfolio slug is already in use");
        } catch (OptimisticLockingFailureException ex) {
            throw new ApiException(HttpStatus.CONFLICT, "PORTFOLIO_WRITE_CONFLICT", "Portfolio was updated by another request");
        }
        return toResponse(portfolio, revision);
    }

    @Transactional
    public void archive(UUID userId, UUID portfolioId) {
        Portfolio portfolio = ownedPortfolio(userId, portfolioId);
        portfolio.archive();
        portfolioRepository.save(portfolio);
    }

    @Transactional
    public PortfolioDtos.PortfolioResponse publish(UUID userId, UUID portfolioId) {
        Portfolio portfolio = ownedPortfolio(userId, portfolioId);
        PortfolioRevision revision = currentRevision(portfolio);
        portfolio.publishRevision(revision.getId());
        portfolioRepository.save(portfolio);
        return toResponse(portfolio, revision);
    }

    @Transactional
    public PortfolioDtos.PortfolioResponse unpublish(UUID userId, UUID portfolioId) {
        Portfolio portfolio = ownedPortfolio(userId, portfolioId);
        if (portfolio.getPublishedRevisionId() == null) {
            throw new ApiException(HttpStatus.CONFLICT, "PORTFOLIO_NOT_PUBLISHED", "Portfolio is not published");
        }
        portfolio.unpublish();
        portfolioRepository.save(portfolio);
        return toResponse(portfolio, currentRevision(portfolio));
    }

    private Portfolio ownedPortfolio(UUID userId, UUID portfolioId) {
        return portfolioRepository.findByIdAndOwnerUserId(portfolioId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "Portfolio was not found"));
    }

    private PortfolioRevision currentRevision(Portfolio portfolio) {
        UUID revisionId = portfolio.getCurrentDraftRevisionId();
        if (revisionId == null) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "PORTFOLIO_REVISION_MISSING", "Portfolio has no current revision");
        }
        return revisionRepository.findByIdAndPortfolioId(revisionId, portfolio.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "PORTFOLIO_REVISION_MISSING", "Portfolio revision was not found"));
    }

    private void ensureSlugAvailable(UUID userId, String slug, UUID currentId) {
        portfolioRepository.findByOwnerUserIdAndSlug(userId, slug).ifPresent(existing -> {
            if (!existing.getId().equals(currentId)) {
                throw new ApiException(HttpStatus.CONFLICT, "SLUG_ALREADY_EXISTS", "This portfolio slug is already in use");
            }
        });
    }

    private static String normalizeSlug(String raw) {
        return raw.trim().toLowerCase();
    }

    private static JsonNode defaultContent(String title) {
        return JsonNodeFactory.instance.objectNode()
                .put("schemaVersion", PortfolioContentValidator.CURRENT_SCHEMA_VERSION)
                .set("profile", JsonNodeFactory.instance.objectNode().put("displayName", title));
    }

    private PortfolioDtos.PortfolioResponse toResponse(Portfolio p, PortfolioRevision r) {
        return new PortfolioDtos.PortfolioResponse(
                p.getId(), p.getOwnerUserId(), p.getTitle(), p.getSlug(), p.getStatus().name(),
                p.getActiveTemplateVersionId(), p.getCurrentDraftRevisionId(), p.getPublishedRevisionId(), p.getPublishedAt(),
                p.getVersion(), r.getRevisionNumber(), r.getSchemaVersion(), r.getContent(),
                p.getCreatedAt(), p.getUpdatedAt());
    }

    private PortfolioDtos.PortfolioSummaryResponse toSummary(Portfolio p) {
        long revisionNumber = p.getCurrentDraftRevisionId() == null ? 0 :
                revisionRepository.findByIdAndPortfolioId(p.getCurrentDraftRevisionId(), p.getId()).map(PortfolioRevision::getRevisionNumber).orElse(0L);
        return new PortfolioDtos.PortfolioSummaryResponse(
                p.getId(), p.getTitle(), p.getSlug(), p.getStatus().name(), p.getVersion(), revisionNumber, p.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public PortfolioDtos.PublicPortfolioResponse getPublic(String username, String slug) {
        User user = userRepository.findByUsername(username.trim().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "Portfolio was not found"));
        Portfolio portfolio = portfolioRepository.findByOwnerUserIdAndSlug(user.getId(), slug.trim().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "Portfolio was not found"));
        if (portfolio.getStatus() != PortfolioStatus.PUBLISHED || portfolio.getPublishedRevisionId() == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "Portfolio was not found");
        }
        PortfolioRevision revision = revisionRepository.findByIdAndPortfolioId(portfolio.getPublishedRevisionId(), portfolio.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "Portfolio was not found"));
        return new PortfolioDtos.PublicPortfolioResponse(
                portfolio.getId(), user.getUsername(), portfolio.getTitle(), portfolio.getSlug(),
                revision.getSchemaVersion(), revision.getContent(), portfolio.getActiveTemplateVersionId(), portfolio.getPublishedAt());
    }
}
