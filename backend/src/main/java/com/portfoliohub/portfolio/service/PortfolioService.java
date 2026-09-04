package com.portfoliohub.portfolio.service;

import com.portfoliohub.auth.entity.User;
import com.portfoliohub.auth.repository.UserRepository;
import com.portfoliohub.common.api.ApiException;
import com.portfoliohub.portfolio.dto.*;
import com.portfoliohub.portfolio.entity.*;
import com.portfoliohub.portfolio.repository.PortfolioRepository;
import com.portfoliohub.portfolio.repository.PortfolioRevisionRepository;
import com.portfoliohub.template.service.TemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolios;
    private final PortfolioRevisionRepository revisions;
    private final UserRepository users;
    private final TemplateService templates;

    public PortfolioService(PortfolioRepository portfolios,
                            PortfolioRevisionRepository revisions,
                            UserRepository users,
                            TemplateService templates) {
        this.portfolios = portfolios;
        this.revisions = revisions;
        this.users = users;
        this.templates = templates;
    }

    @Transactional
    public PortfolioResponse create(UUID userId, CreatePortfolioRequest r) {
        User u = user(userId);
        String slug = normalizeSlug(r.slug());
        if (portfolios.existsBySlugIgnoreCase(slug)) {
            throw api(HttpStatus.CONFLICT, "PORTFOLIO_SLUG_TAKEN", "Portfolio slug is already taken");
        }
        validateContent(r.schemaVersion(), r.content());
        Portfolio p = new Portfolio();
        p.setOwner(u);
        p.setTitle(r.title());
        p.setSlug(slug);
        p.setStatus(PortfolioStatus.DRAFT);
        portfolios.save(p);
        PortfolioRevision rev = revision(p, u, 1, r.schemaVersion(), r.content());
        p.setCurrentDraftRevision(rev);
        portfolios.save(p);
        return response(p);
    }

    @Transactional(readOnly = true)
    public Page<PortfolioSummaryResponse> list(UUID userId, Pageable pageable) {
        return portfolios.findByOwnerIdAndStatusNotOrderByUpdatedAtDesc(userId, PortfolioStatus.ARCHIVED, pageable)
                .map(this::summary);
    }

    @Transactional(readOnly = true)
    public PortfolioResponse get(UUID userId, UUID id) {
        return response(owned(userId, id));
    }

    @Transactional
    public PortfolioResponse update(UUID userId, UUID id, UpdatePortfolioRequest r) {
        Portfolio p = owned(userId, id);
        if (p.getStatus() == PortfolioStatus.ARCHIVED) {
            throw notFound();
        }
        if (p.getStatus() == PortfolioStatus.PUBLISHING) {
            throw api(HttpStatus.CONFLICT, "PUBLISH_IN_PROGRESS", "Portfolio cannot be edited while publishing is in progress");
        }
        validateContent(r.schemaVersion(), r.content());

        if (p.getActiveTemplateVersionId() != null) {
            templates.requireCompatibleApprovedVersion(p.getActiveTemplateVersionId(), r.schemaVersion());
        }

        long n = revisions.findTopByPortfolioIdOrderByRevisionNumberDesc(p.getId())
                .map(v -> v.getRevisionNumber() + 1)
                .orElse(1L);
        PortfolioRevision rev = revision(p, user(userId), n, r.schemaVersion(), r.content());
        p.setTitle(r.title());
        p.setCurrentDraftRevision(rev);
        if (p.getStatus() == PortfolioStatus.PUBLISHED) {
            p.setStatus(PortfolioStatus.UNPUBLISHED);
        }
        portfolios.save(p);
        return response(p);
    }

    @Transactional
    public PortfolioResponse publish(UUID userId, UUID id) {
        Portfolio p = owned(userId, id);
        PortfolioRevision rev = p.getCurrentDraftRevision();
        if (rev == null) {
            throw api(HttpStatus.CONFLICT, "NO_DRAFT", "Portfolio has no draft revision");
        }
        if (p.getActiveTemplateVersionId() != null) {
            templates.requireCompatibleApprovedVersion(p.getActiveTemplateVersionId(), rev.getSchemaVersion());
        }
        p.setPublishedRevision(rev);
        p.setStatus(PortfolioStatus.PUBLISHED);
        p.setPublishedAt(Instant.now());
        portfolios.save(p);
        return response(p);
    }

    @Transactional
    public PortfolioResponse unpublish(UUID userId, UUID id) {
        Portfolio p = owned(userId, id);
        if (p.getPublishedRevision() == null) {
            throw api(HttpStatus.CONFLICT, "NOT_PUBLISHED", "Portfolio is not published");
        }
        p.setStatus(PortfolioStatus.UNPUBLISHED);
        p.setPublishedAt(null);
        portfolios.save(p);
        return response(p);
    }

    @Transactional
    public void archive(UUID userId, UUID id) {
        Portfolio p = owned(userId, id);
        p.setStatus(PortfolioStatus.ARCHIVED);
        portfolios.save(p);
    }

    @Transactional(readOnly = true)
    public PublicPortfolioResponse publicPortfolio(String slug) {
        Portfolio p = portfolios.findBySlugAndStatus(normalizeSlug(slug), PortfolioStatus.PUBLISHED)
                .orElseThrow(this::notFound);
        PortfolioRevision r = p.getPublishedRevision();
        if (r == null) {
            throw notFound();
        }
        return new PublicPortfolioResponse(p.getId(), p.getSlug(), p.getTitle(), r.getSchemaVersion(),
                r.getContent(), p.getActiveTemplateVersionId(), p.getPublishedAt());
    }

    private Portfolio owned(UUID uid, UUID id) {
        return portfolios.findByOwnerIdAndId(uid, id).orElseThrow(this::notFound);
    }

    private User user(UUID id) {
        return users.findById(id).orElseThrow(this::notFound);
    }

    private PortfolioRevision revision(Portfolio p, User u, long n, String schema, JsonNode c) {
        PortfolioRevision r = new PortfolioRevision();
        r.setPortfolio(p);
        r.setCreatedBy(u);
        r.setRevisionNumber(n);
        r.setSchemaVersion(schema);
        r.setContent(c);
        return revisions.save(r);
    }

    private PortfolioSummaryResponse summary(Portfolio p) {
        long n = p.getCurrentDraftRevision() == null ? 0 : p.getCurrentDraftRevision().getRevisionNumber();
        return new PortfolioSummaryResponse(p.getId(), p.getTitle(), p.getSlug(), p.getStatus(), n, p.getUpdatedAt());
    }

    private PortfolioResponse response(Portfolio p) {
        return new PortfolioResponse(
                p.getId(),
                p.getTitle(),
                p.getSlug(),
                p.getStatus(),
                p.getCurrentDraftRevision() == null ? 0 : p.getCurrentDraftRevision().getRevisionNumber(),
                p.getPublishedRevision() == null ? null : p.getPublishedRevision().getId(),
                p.getActiveTemplateVersionId(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getPublishedAt(),
                toRev(p.getCurrentDraftRevision()),
                toRev(p.getPublishedRevision()));
    }

    private RevisionResponse toRev(PortfolioRevision r) {
        return r == null ? null : new RevisionResponse(r.getId(), r.getRevisionNumber(), r.getSchemaVersion(), r.getContent(), r.getCreatedAt());
    }

    private void validateContent(String schema, JsonNode content) {
        if (content == null || !content.isObject()) {
            throw api(HttpStatus.BAD_REQUEST, "INVALID_PORTFOLIO_CONTENT", "Portfolio content must be a JSON object");
        }
        if (!content.has("schemaVersion") || !schema.equals(content.path("schemaVersion").asText())) {
            throw api(HttpStatus.BAD_REQUEST, "SCHEMA_VERSION_MISMATCH", "Request schemaVersion must match content.schemaVersion");
        }
    }

    private String normalizeSlug(String s) {
        return s.trim().toLowerCase();
    }

    private ApiException notFound() {
        return api(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "Portfolio was not found");
    }

    private ApiException api(HttpStatus s, String c, String m) {
        return new ApiException(s, c, m);
    }
}
