package com.portfoliohub.publishing.service;

import com.portfoliohub.portfolio.entity.Portfolio;
import com.portfoliohub.portfolio.entity.PortfolioStatus;
import com.portfoliohub.portfolio.repository.PortfolioRepository;
import com.portfoliohub.publishing.build.BuildArtifact;
import com.portfoliohub.publishing.build.StaticPortfolioBuilder;
import com.portfoliohub.publishing.deploy.DeploymentProvider;
import com.portfoliohub.publishing.deploy.DeploymentResult;
import com.portfoliohub.publishing.entity.PublishJob;
import com.portfoliohub.publishing.entity.PublishJobStatus;
import com.portfoliohub.publishing.repository.PublishJobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PublishingWorker {
    private final PublishJobRepository jobs;
    private final PortfolioRepository portfolios;
    private final StaticPortfolioBuilder builder;
    private final DeploymentProvider deploymentProvider;

    public PublishingWorker(PublishJobRepository jobs,
                            PortfolioRepository portfolios,
                            StaticPortfolioBuilder builder,
                            DeploymentProvider deploymentProvider) {
        this.jobs = jobs;
        this.portfolios = portfolios;
        this.builder = builder;
        this.deploymentProvider = deploymentProvider;
    }

    @Async("publishingTaskExecutor")
    @Transactional
    public void processAsync(UUID jobId) {
        PublishJob job = jobs.findById(jobId).orElse(null);
        if (job == null || job.getStatus() != PublishJobStatus.QUEUED) return;

        try {
            job.setStatus(PublishJobStatus.RUNNING);
            job.setStartedAt(Instant.now());
            jobs.save(job);

            Portfolio portfolio = job.getPortfolio();
            BuildArtifact artifact = builder.build(portfolio, job.getRevision(), job.getTemplateVersion(), job.getId().toString());
            DeploymentResult deployment = deploymentProvider.deploy(portfolio, artifact);

            job.setArtifactPath(deployment.publishedDirectory().toString());
            job.setPublicPath(deployment.publicPath());
            job.setContentHash(artifact.contentHash());
            job.setStatus(PublishJobStatus.SUCCEEDED);
            job.setCompletedAt(Instant.now());
            jobs.save(job);

            portfolio.setStatus(PortfolioStatus.PUBLISHED);
            portfolio.setPublishedAt(Instant.now());
            portfolios.save(portfolio);
        } catch (Exception ex) {
            job.setStatus(PublishJobStatus.FAILED);
            job.setCompletedAt(Instant.now());
            job.setErrorMessage(truncate(ex.getMessage()));
            jobs.save(job);

            Portfolio portfolio = job.getPortfolio();
            portfolio.setStatus(PortfolioStatus.PUBLISH_FAILED);
            portfolio.setPublishedAt(null);
            portfolios.save(portfolio);
        }
    }

    @Transactional
    public void undeployAfterValidation(Portfolio portfolio) {
        deploymentProvider.undeploy(portfolio);
    }

    private String truncate(String message) {
        if (message == null) return "Publishing failed";
        return message.length() <= 4000 ? message : message.substring(0, 4000);
    }
}
