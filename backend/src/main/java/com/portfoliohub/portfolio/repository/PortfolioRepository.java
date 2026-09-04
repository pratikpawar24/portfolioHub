package com.portfoliohub.portfolio.repository;

import com.portfoliohub.portfolio.entity.Portfolio;
import com.portfoliohub.portfolio.entity.PortfolioStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    Page<Portfolio> findByOwnerIdAndStatusNotOrderByUpdatedAtDesc(UUID ownerId, PortfolioStatus excluded, Pageable pageable);
    Optional<Portfolio> findByOwnerIdAndId(UUID ownerId, UUID id);
    Optional<Portfolio> findBySlugAndStatus(String slug, PortfolioStatus status);
    boolean existsBySlugIgnoreCase(String slug);
    long countByActiveTemplateVersionId(UUID templateVersionId);
}
