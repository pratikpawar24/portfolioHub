package com.portfoliohub.portfolio.repository;
import com.portfoliohub.portfolio.entity.PortfolioRevision; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface PortfolioRevisionRepository extends JpaRepository<PortfolioRevision, UUID> { Optional<PortfolioRevision> findTopByPortfolioIdOrderByRevisionNumberDesc(UUID portfolioId); }
