package com.portfoliohub.publishing.repository;

import com.portfoliohub.publishing.entity.PublishJob;
import com.portfoliohub.publishing.entity.PublishJobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PublishJobRepository extends JpaRepository<PublishJob, UUID> {
    Optional<PublishJob> findFirstByPortfolioIdOrderByCreatedAtDesc(UUID portfolioId);
    Page<PublishJob> findByPortfolioIdOrderByCreatedAtDesc(UUID portfolioId, Pageable pageable);
    long countByStatus(PublishJobStatus status);
}
