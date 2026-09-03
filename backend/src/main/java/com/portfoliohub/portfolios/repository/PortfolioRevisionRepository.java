package com.portfoliohub.portfolios.repository;

import com.portfoliohub.portfolios.domain.PortfolioRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioRevisionRepository extends JpaRepository<PortfolioRevision, UUID> {
    Optional<PortfolioRevision> findByIdAndPortfolioId(UUID id, UUID portfolioId);
    Optional<PortfolioRevision> findTopByPortfolioIdOrderByRevisionNumberDesc(UUID portfolioId);
}
