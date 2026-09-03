package com.portfoliohub.portfolios.repository;

import com.portfoliohub.portfolios.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    List<Portfolio> findAllByOwnerUserIdOrderByUpdatedAtDesc(UUID ownerUserId);
    Optional<Portfolio> findByIdAndOwnerUserId(UUID id, UUID ownerUserId);
    Optional<Portfolio> findByOwnerUserIdAndSlug(UUID ownerUserId, String slug);
    boolean existsByOwnerUserIdAndSlug(UUID ownerUserId, String slug);
}
