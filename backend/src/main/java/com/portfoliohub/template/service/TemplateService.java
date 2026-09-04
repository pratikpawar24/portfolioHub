package com.portfoliohub.template.service;

import com.portfoliohub.auth.entity.User;
import com.portfoliohub.auth.repository.UserRepository;
import com.portfoliohub.common.api.ApiException;
import com.portfoliohub.template.dto.*;
import com.portfoliohub.template.entity.*;
import com.portfoliohub.template.repository.TemplateRepository;
import com.portfoliohub.template.repository.TemplateVersionRepository;
import com.portfoliohub.marketplace.service.MarketplaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class TemplateService {
    private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:[.-].*)?$");

    private final TemplateRepository templates;
    private final TemplateVersionRepository versions;
    private final UserRepository users;
    private final MarketplaceService marketplace;

    public TemplateService(TemplateRepository templates, TemplateVersionRepository versions, UserRepository users, MarketplaceService marketplace) {
        this.templates = templates;
        this.versions = versions;
        this.users = users;
        this.marketplace = marketplace;
    }

    @Transactional(readOnly = true)
    public Page<TemplateSummaryResponse> listPublic(Pageable pageable) {
        return templates.findByStatusAndVisibility(TemplateStatus.ACTIVE, TemplateVisibility.PUBLIC, pageable).map(this::summary);
    }

    @Transactional(readOnly = true)
    public TemplateResponse getPublic(String slug) {
        Template template = templates.findBySlugIgnoreCaseAndStatus(slug.trim().toLowerCase(), TemplateStatus.ACTIVE)
                .filter(t -> t.getVisibility() == TemplateVisibility.PUBLIC)
                .orElseThrow(this::notFound);
        return response(template);
    }

    @Transactional
    public TemplateResponse registerFirstParty(UUID adminUserId, RegisterTemplateRequest request) {
        if (templates.existsBySlugIgnoreCase(request.slug())) {
            throw api(HttpStatus.CONFLICT, "TEMPLATE_SLUG_TAKEN", "Template slug is already taken");
        }

        validateVersion(request.version(), "version");
        validateSchemaRange(request.schemaMin(), request.schemaMax());
        validateManifest(request.manifest(), request);
        User creator = users.findById(adminUserId).orElseThrow(this::notFound);

        Template template = new Template();
        template.setCreator(creator);
        template.setSlug(request.slug());
        template.setName(request.name());
        template.setDescription(request.description());
        template.setCategory(request.category());
        template.setFramework(request.manifest().path("framework").asText());
        template.setLicense(request.license());
        template.setRepositoryUrl(request.repositoryUrl());
        template.setVisibility(request.visibility());
        template.setStatus(TemplateStatus.ACTIVE);
        template = templates.save(template);

        TemplateVersion version = new TemplateVersion();
        version.setTemplate(template);
        version.setVersion(request.version());
        version.setManifest(request.manifest());
        version.setSchemaMin(request.schemaMin());
        version.setSchemaMax(request.schemaMax());
        version.setSourceReference(request.sourceReference());
        version.setArtifactReference(request.artifactReference());
        version.setPreviewReference(request.previewReference());
        version.setStatus(TemplateVersionStatus.APPROVED);
        versions.save(version);
        marketplace.initializeStats(template);

        return response(template);
    }

    @Transactional(readOnly = true)
    public TemplateVersionResponse getApprovedVersion(UUID templateVersionId) {
        return versions.findByIdAndStatus(templateVersionId, TemplateVersionStatus.APPROVED)
                .map(this::versionResponse)
                .orElseThrow(this::notFound);
    }

    @Transactional(readOnly = true)
    public TemplateVersion requireCompatibleApprovedVersion(UUID templateVersionId, String portfolioSchemaVersion) {
        TemplateVersion version = versions.findByIdAndStatus(templateVersionId, TemplateVersionStatus.APPROVED)
                .orElseThrow(this::notFound);
        if (version.getTemplate().getStatus() != TemplateStatus.ACTIVE || version.getTemplate().getVisibility() != TemplateVisibility.PUBLIC) {
            throw notFound();
        }
        if (!isSchemaCompatible(portfolioSchemaVersion, version.getSchemaMin(), version.getSchemaMax())) {
            throw api(HttpStatus.CONFLICT, "TEMPLATE_SCHEMA_INCOMPATIBLE",
                    "Template version is not compatible with the portfolio schema version");
        }
        return version;
    }

    private boolean isSchemaCompatible(String portfolioSchema, String min, String max) {
        try {
            SemVer p = SemVer.parseSchema(portfolioSchema, false);
            SemVer low = SemVer.parseSchema(min, false);
            SemVer high = SemVer.parseSchema(max, true);
            return p.compareTo(low) >= 0 && p.compareTo(high) <= 0;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    private void validateManifest(JsonNode manifest, RegisterTemplateRequest request) {
        if (!manifest.isObject()) {
            throw api(HttpStatus.BAD_REQUEST, "INVALID_TEMPLATE_MANIFEST", "Template manifest must be a JSON object");
        }
        requireText(manifest, "manifestVersion");
        requireText(manifest, "name");
        requireText(manifest, "version");
        requireText(manifest, "license");
        requireText(manifest, "framework");
        requireText(manifest, "runtime");
        if (!java.util.Set.of("html-static", "react-vite", "next-static", "astro-static")
                .contains(manifest.path("framework").asText())) {
            throw api(HttpStatus.BAD_REQUEST, "UNSUPPORTED_TEMPLATE_FRAMEWORK",
                    "Unsupported template framework");
        }
        if (!"static".equalsIgnoreCase(manifest.path("runtime").asText())) {
            throw api(HttpStatus.BAD_REQUEST, "UNSUPPORTED_TEMPLATE_RUNTIME",
                    "B3 only accepts static template runtimes");
        }
        JsonNode build = manifest.get("build");
        if (build == null || !build.isObject()) {
            throw api(HttpStatus.BAD_REQUEST, "INVALID_TEMPLATE_MANIFEST", "Template manifest build object is required");
        }
        requireText(build, "packageManager");
        requireText(build, "nodeVersion");
        requireText(build, "command");
        requireText(build, "outputDirectory");

        if (!request.version().equals(manifest.path("version").asText())) {
            throw api(HttpStatus.BAD_REQUEST, "MANIFEST_VERSION_MISMATCH", "Request version must match manifest.version");
        }
        if (!request.name().equals(manifest.path("name").asText())) {
            throw api(HttpStatus.BAD_REQUEST, "MANIFEST_NAME_MISMATCH", "Request name must match manifest.name");
        }
        if (!request.license().equals(manifest.path("license").asText())) {
            throw api(HttpStatus.BAD_REQUEST, "MANIFEST_LICENSE_MISMATCH", "Request license must match manifest.license");
        }
        if (!request.schemaMin().equals(manifest.path("portfolioSchema").path("min").asText())) {
            throw api(HttpStatus.BAD_REQUEST, "MANIFEST_SCHEMA_MISMATCH", "Request schemaMin must match manifest.portfolioSchema.min");
        }
        if (!request.schemaMax().equals(manifest.path("portfolioSchema").path("max").asText())) {
            throw api(HttpStatus.BAD_REQUEST, "MANIFEST_SCHEMA_MISMATCH", "Request schemaMax must match manifest.portfolioSchema.max");
        }
    }

    private void requireText(JsonNode object, String name) {
        if (!object.hasNonNull(name) || !object.path(name).isTextual() || object.path(name).asText().isBlank()) {
            throw api(HttpStatus.BAD_REQUEST, "INVALID_TEMPLATE_MANIFEST", "Manifest field '" + name + "' is required");
        }
    }

    private void validateSchemaRange(String min, String max) {
        try {
            SemVer low = SemVer.parseSchema(min, false);
            SemVer high = SemVer.parseSchema(max, true);
            if (low.compareTo(high) > 0) {
                throw api(HttpStatus.BAD_REQUEST, "INVALID_SCHEMA_RANGE", "schemaMin must not be greater than schemaMax");
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            throw api(HttpStatus.BAD_REQUEST, "INVALID_SCHEMA_RANGE", "Schema versions must use forms such as 1.0, 1.0.0, or 1.x");
        }
    }

    private void validateVersion(String version, String field) {
        if (!VERSION_PATTERN.matcher(version).matches()) {
            throw api(HttpStatus.BAD_REQUEST, "INVALID_SEMVER", field + " must use semantic version x.y.z");
        }
    }

    private TemplateSummaryResponse summary(Template t) {
        return new TemplateSummaryResponse(t.getId(), t.getSlug(), t.getName(), t.getDescription(), t.getCategory(),
                t.getLicense(), t.getVisibility(), t.getStatus(), t.getUpdatedAt());
    }

    private TemplateResponse response(Template t) {
        List<TemplateVersionResponse> versionResponses = versions.findByTemplateIdOrderByVersionDesc(t.getId())
                .stream().map(this::versionResponse).toList();
        return new TemplateResponse(t.getId(), t.getCreator() == null ? null : t.getCreator().getId(), t.getSlug(), t.getName(),
                t.getDescription(), t.getCategory(), t.getLicense(), t.getRepositoryUrl(), t.getVisibility(), t.getStatus(),
                t.getParentTemplate() == null ? null : t.getParentTemplate().getId(),
                t.getOriginalTemplate() == null ? null : t.getOriginalTemplate().getId(),
                t.getCreatedAt(), t.getUpdatedAt(), versionResponses);
    }

    private TemplateVersionResponse versionResponse(TemplateVersion v) {
        return new TemplateVersionResponse(v.getId(), v.getTemplate().getId(), v.getVersion(), v.getManifest(), v.getSchemaMin(),
                v.getSchemaMax(), v.getSourceReference(), v.getArtifactReference(), v.getPreviewReference(), v.getStatus(), v.getCreatedAt());
    }

    private ApiException notFound() {
        return api(HttpStatus.NOT_FOUND, "TEMPLATE_NOT_FOUND", "Template was not found");
    }

    private ApiException api(HttpStatus status, String code, String message) {
        return new ApiException(status, code, message);
    }

    private record SemVer(int major, int minor, int patch) implements Comparable<SemVer> {
        static SemVer parseSchema(String value, boolean upperBound) {
            String normalized = value.trim().toLowerCase();
            String[] parts = normalized.split("\\.");
            int major = Integer.parseInt(parts[0]);
            boolean minorWildcard = parts.length == 1 || parts[1].equals("x");
            int minor = minorWildcard ? (upperBound ? Integer.MAX_VALUE : 0) : Integer.parseInt(parts[1]);
            boolean patchWildcard = parts.length < 3 || parts[2].equals("x");
            int patch = patchWildcard ? (upperBound ? Integer.MAX_VALUE : 0) : Integer.parseInt(parts[2]);
            return new SemVer(major, minor, patch);
        }
        @Override public int compareTo(SemVer other) {
            int c = Integer.compare(major, other.major);
            if (c != 0) return c;
            c = Integer.compare(minor, other.minor);
            return c != 0 ? c : Integer.compare(patch, other.patch);
        }
    }
}
