package com.portfoliohub.portfolio.dto;
import com.portfoliohub.portfolio.entity.PortfolioStatus; import java.time.Instant; import java.util.UUID;
public record PortfolioSummaryResponse(UUID id,String title,String slug,PortfolioStatus status,long currentRevision,Instant updatedAt) {}
