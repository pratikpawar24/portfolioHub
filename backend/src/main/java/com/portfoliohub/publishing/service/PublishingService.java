package com.portfoliohub.publishing.service;

import com.portfoliohub.common.api.ApiException;
import com.portfoliohub.portfolio.entity.Portfolio;
import com.portfoliohub.portfolio.entity.PortfolioRevision;
import com.portfoliohub.portfolio.entity.PortfolioStatus;
import com.portfoliohub.portfolio.repository.PortfolioRepository;
import com.portfoliohub.publishing.dto.PublishJobResponse;
import com.portfoliohub.publishing.entity.PublishJob;
import com.portfoliohub.publishing.entity.PublishJobStatus;
import com.portfoliohub.publishing.repository.PublishJobRepository;
import com.portfoliohub.template.entity.TemplateVersion;
import com.portfoliohub.template.entity.TemplateVersionStatus;
import com.portfoliohub.template.repository.TemplateVersionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Service
public class PublishingService {
    private final PortfolioRepository portfolios;
    private final PublishJobRepository jobs;
    private final TemplateVersionRepository versions;
    private final PublishingWorker worker;

    public PublishingService(PortfolioRepository portfolios,
                             PublishJobRepository jobs,
                             TemplateVersionRepository versions,
                             PublishingWorker worker) {
        this.portfolios = portfolios;
        this.jobs = jobs;
        this.versions = versions;
        this.worker = worker;
    }

    @Transactional
    public PublishJobResponse requestPublish(UUID userId, UUID portfolioId) {
        Portfolio portfolio = portfolios.findByOwnerIdAndId(userId, portfolioId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "Portfolio was not found"));
        if (portfolio.getStatus() == PortfolioStatus.ARCHIVED) {
            throw new ApiException(HttpStatus.CONFLICT, "PORTFOLIO_ARCHIVED", "Archived portfolios cannot be published");
        }
        if (portfolio.getStatus() == PortfolioStatus.PUBLISHING) {
            throw new ApiException(HttpStatus.CONFLICT, "PUBLISH_ALREADY_RUNNING", "A publishing job is already running for this portfolio");
        }

        PortfolioRevision revision = portfolio.getCurrentDraftRevision();
        if (revision == null) {
            throw new ApiException(HttpStatus.CONFLICT, "NO_DRAFT", "Portfolio has no draft revision");
        }

        TemplateVersion version = null;
        if (portfolio.getActiveTemplateVersionId() != null) {
            version = versions.findById(portfolio.getActiveTemplateVersionId())
                    .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "TEMPLATE_NOT_FOUND", "Selected template version was not found"));
            if (version.getStatus() != TemplateVersionStatus.APPROVED
                    || version.getTemplate().getStatus() != com.portfoliohub.template.entity.TemplateStatus.ACTIVE
                    || version.getTemplate().getVisibility() != com.portfoliohub.template.entity.TemplateVisibility.PUBLIC) {
                throw new ApiException(HttpStatus.CONFLICT, "TEMPLATE_NOT_AVAILABLE", "Selected template version is not available for publishing");
            }
        }

        PublishJob job = new PublishJob();
        job.setPortfolio(portfolio);
        job.setRevision(revision);
        job.setTemplateVersion(version);
        job = jobs.save(job);

        portfolio.setPublishedRevision(revision);
        portfolio.setStatus(PortfolioStatus.PUBLISHING);
        portfolio.setPublishedAt(null);
        portfolios.save(portfolio);

        UUID jobId = job.getId();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    worker.processAsync(jobId);
                }
            });
        } else {
            worker.processAsync(jobId);
        }
        return toResponse(job);
    }

    @Transactional(readOnly = true)
    public PublishJobResponse getJob(UUID userId, UUID jobId) {
        PublishJob job = jobs.findById(jobId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PUBLISH_JOB_NOT_FOUND", "Publishing job was not found"));
        if (!job.getPortfolio().getOwner().getId().equals(userId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "PUBLISH_JOB_NOT_FOUND", "Publishing job was not found");
        }
        return toResponse(job);
    }

    @Transactional(readOnly = true)
    public Page<PublishJobResponse> listJobs(UUID userId, UUID portfolioId, Pageable pageable) {
        Portfolio portfolio = portfolios.findByOwnerIdAndId(userId, portfolioId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "Portfolio was not found"));
        return jobs.findByPortfolioIdOrderByCreatedAtDesc(portfolio.getId(), pageable).map(this::toResponse);
    }

    @Transactional
    public void unpublish(UUID userId, UUID portfolioId) {
        Portfolio portfolio = portfolios.findByOwnerIdAndId(userId, portfolioId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND", "Portfolio was not found"));
        if (portfolio.getPublishedRevision() == null || portfolio.getStatus() != PortfolioStatus.PUBLISHED) {
            throw new ApiException(HttpStatus.CONFLICT, "NOT_PUBLISHED", "Portfolio is not published");
        }
        worker.undeployAfterValidation(portfolio);
        portfolio.setStatus(PortfolioStatus.UNPUBLISHED);
        portfolio.setPublishedAt(null);
        portfolios.save(portfolio);
    }

    private PublishJobResponse toResponse(PublishJob job) {
        String publicUrl = job.getPublicPath() == null ? null : job.getPublicPath() + "/";
        return new PublishJobResponse(job.getId(), job.getPortfolio().getId(), job.getRevision().getId(),
                job.getTemplateVersion() == null ? null : job.getTemplateVersion().getId(), job.getStatus(),
                job.getCreatedAt(), job.getStartedAt(), job.getCompletedAt(), publicUrl, job.getErrorMessage());
    }
}
