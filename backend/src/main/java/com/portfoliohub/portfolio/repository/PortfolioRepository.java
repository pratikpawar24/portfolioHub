package com.portfoliohub.portfolio.repository;
import com.portfoliohub.portfolio.entity.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
 Page<Portfolio> findByOwnerIdAndStatusNotOrderByUpdatedAtDesc(UUID ownerId, PortfolioStatus excluded, Pageable pageable);
 Optional<Portfolio> findByOwnerIdAndId(UUID ownerId, UUID id);
 Optional<Portfolio> findBySlugAndStatus(String slug, PortfolioStatus status);
 boolean existsBySlugIgnoreCase(String slug);
}
