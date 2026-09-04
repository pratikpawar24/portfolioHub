package com.portfoliohub.marketplace.repository;

import com.portfoliohub.marketplace.entity.TemplateLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TemplateLikeRepository extends JpaRepository<TemplateLike, UUID> {
    boolean existsByUserIdAndTemplateId(UUID userId, UUID templateId);
    long countByTemplateId(UUID templateId);
    void deleteByUserIdAndTemplateId(UUID userId, UUID templateId);
}
