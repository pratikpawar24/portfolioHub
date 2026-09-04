package com.portfoliohub.publishing.service;

import com.portfoliohub.publishing.entity.PublishJob;
import com.portfoliohub.publishing.repository.PublishJobRepository;
import com.portfoliohub.portfolio.entity.Portfolio;
import com.portfoliohub.portfolio.entity.PortfolioRevision;
import com.portfoliohub.portfolio.entity.PortfolioStatus;
import com.portfoliohub.portfolio.repository.PortfolioRepository;
import com.portfoliohub.template.repository.TemplateVersionRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PublishingServiceTest {

    @Test
    void requestPublishQueuesJobAndMarksPortfolioPublishing() {
        PortfolioRepository portfolios = mock(PortfolioRepository.class);
        PublishJobRepository jobs = mock(PublishJobRepository.class);
        TemplateVersionRepository versions = mock(TemplateVersionRepository.class);
        PublishingWorker worker = mock(PublishingWorker.class);

        PublishingService service = new PublishingService(portfolios, jobs, versions, worker);

        UUID userId = UUID.randomUUID();
        UUID portfolioId = UUID.randomUUID();
        Portfolio portfolio = new Portfolio();
        portfolio.setTitle("Test Portfolio");
        portfolio.setSlug("test-portfolio");
        portfolio.setStatus(PortfolioStatus.DRAFT);

        PortfolioRevision revision = new PortfolioRevision();
        revision.setPortfolio(portfolio);
        revision.setRevisionNumber(1);
        revision.setSchemaVersion("1.0");
        portfolio.setCurrentDraftRevision(revision);

        PublishJob saved = new PublishJob();
        saved.setPortfolio(portfolio);
        saved.setRevision(revision);

        when(portfolios.findByOwnerIdAndId(userId, portfolioId)).thenReturn(Optional.of(portfolio));
        when(jobs.save(any(PublishJob.class))).thenReturn(saved);

        service.requestPublish(userId, portfolioId);

        assertEquals(PortfolioStatus.PUBLISHING, portfolio.getStatus());
        assertEquals(revision, portfolio.getPublishedRevision());
        verify(jobs).save(any(PublishJob.class));
        verify(worker).processAsync(null);
    }

    @Test
    void requestPublishRejectsConcurrentPublish() {
        PortfolioRepository portfolios = mock(PortfolioRepository.class);
        PublishJobRepository jobs = mock(PublishJobRepository.class);
        TemplateVersionRepository versions = mock(TemplateVersionRepository.class);
        PublishingWorker worker = mock(PublishingWorker.class);

        PublishingService service = new PublishingService(portfolios, jobs, versions, worker);

        UUID userId = UUID.randomUUID();
        UUID portfolioId = UUID.randomUUID();
        Portfolio portfolio = new Portfolio();
        portfolio.setSlug("already-publishing");
        portfolio.setStatus(PortfolioStatus.PUBLISHING);
        when(portfolios.findByOwnerIdAndId(userId, portfolioId)).thenReturn(Optional.of(portfolio));

        assertThrows(com.portfoliohub.common.api.ApiException.class,
                () -> service.requestPublish(userId, portfolioId));

        verifyNoInteractions(jobs, versions, worker);
    }
}
