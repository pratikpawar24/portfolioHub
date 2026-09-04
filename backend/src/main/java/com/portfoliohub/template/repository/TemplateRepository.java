package com.portfoliohub.template.repository;

import com.portfoliohub.template.entity.Template;
import com.portfoliohub.template.entity.TemplateStatus;
import com.portfoliohub.template.entity.TemplateVisibility;
import com.portfoliohub.template.entity.TemplateDerivationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TemplateRepository extends JpaRepository<Template, UUID> {
    boolean existsBySlugIgnoreCase(String slug);
    Optional<Template> findBySlugIgnoreCaseAndStatus(String slug, TemplateStatus status);
    Page<Template> findByStatusAndVisibility(TemplateStatus status, TemplateVisibility visibility, Pageable pageable);

    @Query("""
        select t from Template t
        where t.status = :status and t.visibility = :visibility
          and (:q is null or lower(t.name) like lower(concat('%', :q, '%'))
               or lower(coalesce(t.description, '')) like lower(concat('%', :q, '%'))
               or lower(t.category) like lower(concat('%', :q, '%')))
          and (:category is null or lower(t.category) = lower(:category))
          and (:framework is null or lower(t.framework) = lower(:framework))
        """)
    Page<Template> searchMarketplace(
            @Param("status") TemplateStatus status,
            @Param("visibility") TemplateVisibility visibility,
            @Param("q") String q,
            @Param("category") String category,
            @Param("framework") String framework,
            Pageable pageable);

    @Query("""
        select t from Template t
        left join com.portfoliohub.marketplace.entity.TemplateMarketplaceStats s on s.templateId = t.id
        where t.status = :status and t.visibility = :visibility
          and (:q is null or lower(t.name) like lower(concat('%', :q, '%'))
               or lower(coalesce(t.description, '')) like lower(concat('%', :q, '%'))
               or lower(coalesce(t.category, '')) like lower(concat('%', :q, '%')))
          and (:category is null or lower(t.category) = lower(:category))
          and (:framework is null or lower(t.framework) = lower(:framework))
        order by (coalesce(s.likeCount, 0) * 3 + coalesce(s.favoriteCount, 0) * 2 + coalesce(s.usageCount, 0) * 5 + coalesce(s.forkCount, 0) * 4 + coalesce(s.remixCount, 0) * 4) desc,
                 t.updatedAt desc
        """)
    Page<Template> searchMarketplacePopular(
            @Param("status") TemplateStatus status,
            @Param("visibility") TemplateVisibility visibility,
            @Param("q") String q,
            @Param("category") String category,
            @Param("framework") String framework,
            Pageable pageable);

    long countByCreatorIdAndStatus(UUID creatorId, TemplateStatus status);

    long countByParentTemplateIdAndDerivationType(UUID parentTemplateId, TemplateDerivationType derivationType);
}
