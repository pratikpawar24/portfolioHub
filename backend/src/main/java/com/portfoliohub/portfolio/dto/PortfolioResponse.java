package com.portfoliohub.portfolio.dto;
import com.portfoliohub.portfolio.entity.PortfolioStatus; import java.time.Instant; import java.util.UUID;
public record PortfolioResponse(UUID id,String title,String slug,PortfolioStatus status,long currentRevision,UUID publishedRevisionId,UUID activeTemplateVersionId,Instant createdAt,Instant updatedAt,Instant publishedAt,RevisionResponse currentDraft,RevisionResponse publishedRevision) {}
