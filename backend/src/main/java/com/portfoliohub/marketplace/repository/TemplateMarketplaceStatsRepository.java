package com.portfoliohub.marketplace.repository;

import com.portfoliohub.marketplace.entity.TemplateMarketplaceStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TemplateMarketplaceStatsRepository extends JpaRepository<TemplateMarketplaceStats, UUID> {
}
