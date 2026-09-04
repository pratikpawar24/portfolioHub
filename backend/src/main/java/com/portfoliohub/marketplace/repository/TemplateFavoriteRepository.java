package com.portfoliohub.marketplace.repository;

import com.portfoliohub.marketplace.entity.TemplateFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TemplateFavoriteRepository extends JpaRepository<TemplateFavorite, UUID> {
    boolean existsByUserIdAndTemplateId(UUID userId, UUID templateId);
    java.util.Optional<TemplateFavorite> findByUserIdAndTemplateId(UUID userId, UUID templateId);
    Page<TemplateFavorite> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    long countByTemplateId(UUID templateId);
    void deleteByUserIdAndTemplateId(UUID userId, UUID templateId);
}
