package com.portfoliohub.marketplace.service;

import com.portfoliohub.auth.entity.User;
import com.portfoliohub.auth.repository.UserRepository;
import com.portfoliohub.common.api.ApiException;
import com.portfoliohub.marketplace.dto.*;
import com.portfoliohub.marketplace.entity.TemplateFavorite;
import com.portfoliohub.marketplace.entity.TemplateLike;
import com.portfoliohub.marketplace.entity.TemplateMarketplaceStats;
import com.portfoliohub.marketplace.repository.TemplateFavoriteRepository;
import com.portfoliohub.marketplace.repository.TemplateLikeRepository;
import com.portfoliohub.marketplace.repository.TemplateMarketplaceStatsRepository;
import com.portfoliohub.portfolio.repository.PortfolioRepository;
import com.portfoliohub.template.entity.Template;
import com.portfoliohub.template.entity.TemplateDerivationType;
import com.portfoliohub.template.entity.TemplateStatus;
import com.portfoliohub.template.entity.TemplateVisibility;
import com.portfoliohub.template.repository.TemplateRepository;
import com.portfoliohub.template.repository.TemplateVersionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MarketplaceService {
    private final TemplateRepository templates;
    private final TemplateVersionRepository versions;
    private final TemplateLikeRepository likes;
    private final TemplateFavoriteRepository favorites;
    private final TemplateMarketplaceStatsRepository stats;
    private final UserRepository users;
    private final PortfolioRepository portfolios;

    public MarketplaceService(TemplateRepository templates,
                              TemplateVersionRepository versions,
                              TemplateLikeRepository likes,
                              TemplateFavoriteRepository favorites,
                              TemplateMarketplaceStatsRepository stats,
                              UserRepository users,
                              PortfolioRepository portfolios) {
        this.templates = templates;
        this.versions = versions;
        this.likes = likes;
        this.favorites = favorites;
        this.stats = stats;
        this.users = users;
        this.portfolios = portfolios;
    }

    @Transactional(readOnly = true)
    public Page<TemplateMarketplaceResponse> search(String query, String category, String framework, String sort, Pageable pageable, UUID currentUserId) {
        String q = normalizeNullable(query);
        String c = normalizeNullable(category);
        String f = normalizeNullable(framework);
        Page<Template> result = "popular".equalsIgnoreCase(sort)
                ? templates.searchMarketplacePopular(TemplateStatus.ACTIVE, TemplateVisibility.PUBLIC, q, c, f, pageable)
                : templates.searchMarketplace(TemplateStatus.ACTIVE, TemplateVisibility.PUBLIC, q, c, f, pageable);
        return result.map(t -> response(t, currentUserId));
    }

    @Transactional(readOnly = true)
    public TemplateMarketplaceResponse get(String slug, UUID currentUserId) {
        Template template = templates.findBySlugIgnoreCaseAndStatus(slug.trim().toLowerCase(), TemplateStatus.ACTIVE)
                .filter(t -> t.getVisibility() == TemplateVisibility.PUBLIC)
                .orElseThrow(() -> notFound("TEMPLATE_NOT_FOUND", "Template was not found"));
        return response(template, currentUserId);
    }

    @Transactional
    public void like(UUID userId, UUID templateId) {
        User user = user(userId);
        Template template = publicTemplate(templateId);
        if (!likes.existsByUserIdAndTemplateId(userId, templateId)) {
            TemplateLike like = new TemplateLike();
            like.setUser(user);
            like.setTemplate(template);
            likes.save(like);
        }
        refreshStats(template);
    }

    @Transactional
    public void unlike(UUID userId, UUID templateId) {
        publicTemplate(templateId);
        if (likes.existsByUserIdAndTemplateId(userId, templateId)) {
            likes.deleteByUserIdAndTemplateId(userId, templateId);
        }
        refreshStatsById(templateId);
    }

    @Transactional
    public void favorite(UUID userId, UUID templateId) {
        User user = user(userId);
        Template template = publicTemplate(templateId);
        if (!favorites.existsByUserIdAndTemplateId(userId, templateId)) {
            TemplateFavorite favorite = new TemplateFavorite();
            favorite.setUser(user);
            favorite.setTemplate(template);
            favorites.save(favorite);
        }
        refreshStats(template);
    }

    @Transactional
    public void unfavorite(UUID userId, UUID templateId) {
        publicTemplate(templateId);
        if (favorites.existsByUserIdAndTemplateId(userId, templateId)) {
            favorites.deleteByUserIdAndTemplateId(userId, templateId);
        }
        refreshStatsById(templateId);
    }

    @Transactional(readOnly = true)
    public Page<FavoriteTemplateResponse> favorites(UUID userId, Pageable pageable) {
        return favorites.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(f -> new FavoriteTemplateResponse(f.getTemplate().getId(), f.getTemplate().getSlug(), f.getTemplate().getName(), f.getCreatedAt()));
    }

    @Transactional
    public void refreshUsage(UUID templateId) {
        Template template = templates.findById(templateId).orElseThrow(() -> notFound("TEMPLATE_NOT_FOUND", "Template was not found"));
        refreshStats(template);
    }


    @Transactional
    public void refreshUsageForTemplateVersion(UUID templateVersionId) {
        var version = versions.findById(templateVersionId)
                .orElseThrow(() -> notFound("TEMPLATE_VERSION_NOT_FOUND", "Template version was not found"));
        refreshStats(version.getTemplate());
    }
    @Transactional
    public void initializeStats(Template template) {
        if (!stats.existsById(template.getId())) {
            TemplateMarketplaceStats value = new TemplateMarketplaceStats();
            value.setTemplate(template);
            stats.save(value);
        }
        refreshStats(template);
    }

    private TemplateMarketplaceResponse response(Template t, UUID currentUserId) {
        TemplateMarketplaceStats s = stats.findById(t.getId()).orElseGet(TemplateMarketplaceStats::new);
        boolean liked = currentUserId != null && likes.existsByUserIdAndTemplateId(currentUserId, t.getId());
        boolean favorited = currentUserId != null && favorites.existsByUserIdAndTemplateId(currentUserId, t.getId());
        return new TemplateMarketplaceResponse(
                t.getId(), t.getSlug(), t.getName(), t.getDescription(), t.getCategory(), t.getFramework(), t.getLicense(), t.getRepositoryUrl(),
                t.getVisibility(), t.getStatus(),
                t.getCreator() == null ? null : t.getCreator().getId(),
                t.getCreator() == null ? null : t.getCreator().getUsername(),
                t.getCreator() == null ? null : t.getCreator().getDisplayName(),
                t.getParentTemplate() == null ? null : t.getParentTemplate().getId(),
                t.getOriginalTemplate() == null ? null : t.getOriginalTemplate().getId(),
                t.getCreatedAt(), t.getUpdatedAt(),
                versions.findByTemplateIdOrderByVersionDesc(t.getId()).stream().map(v -> new com.portfoliohub.template.dto.TemplateVersionResponse(
                        v.getId(), v.getTemplate().getId(), v.getVersion(), v.getManifest(), v.getSchemaMin(), v.getSchemaMax(),
                        v.getSourceReference(), v.getArtifactReference(), v.getPreviewReference(), v.getStatus(), v.getCreatedAt())).toList(),
                new TemplateMarketplaceStatsResponse(
                        s.getLikeCount(), s.getFavoriteCount(), s.getUsageCount(), s.getForkCount(), s.getRemixCount(),
                        popularityScore(s), liked, favorited));
    }

    private long popularityScore(TemplateMarketplaceStats s) {
        return s.getLikeCount() * 3L + s.getFavoriteCount() * 2L + s.getUsageCount() * 5L + s.getForkCount() * 4L + s.getRemixCount() * 4L;
    }

    private void refreshStatsById(UUID templateId) {
        Template template = templates.findById(templateId).orElseThrow(() -> notFound("TEMPLATE_NOT_FOUND", "Template was not found"));
        refreshStats(template);
    }

    private void refreshStats(Template template) {
        TemplateMarketplaceStats value = stats.findById(template.getId()).orElseGet(() -> {
            TemplateMarketplaceStats created = new TemplateMarketplaceStats();
            created.setTemplate(template);
            return created;
        });
        value.setLikeCount(likes.countByTemplateId(template.getId()));
        value.setFavoriteCount(favorites.countByTemplateId(template.getId()));
        value.setUsageCount(versions.findIdsByTemplateId(template.getId()).stream().mapToLong(portfolios::countByActiveTemplateVersionId).sum());
        value.setForkCount(templates.countByParentTemplateIdAndDerivationType(template.getId(), TemplateDerivationType.FORK));
        value.setRemixCount(templates.countByParentTemplateIdAndDerivationType(template.getId(), TemplateDerivationType.REMIX));
        stats.save(value);
    }

    private Template publicTemplate(UUID id) {
        Template t = templates.findById(id).orElseThrow(() -> notFound("TEMPLATE_NOT_FOUND", "Template was not found"));
        if (t.getStatus() != TemplateStatus.ACTIVE || t.getVisibility() != TemplateVisibility.PUBLIC) {
            throw notFound("TEMPLATE_NOT_FOUND", "Template was not found");
        }
        return t;
    }

    private User user(UUID id) {
        return users.findById(id).orElseThrow(() -> notFound("USER_NOT_FOUND", "User was not found"));
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim();
    }

    private ApiException notFound(String code, String message) {
        return new ApiException(HttpStatus.NOT_FOUND, code, message);
    }


}
