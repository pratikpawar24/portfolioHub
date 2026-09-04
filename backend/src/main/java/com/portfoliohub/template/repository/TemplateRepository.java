package com.portfoliohub.template.repository;

import com.portfoliohub.template.entity.Template;
import com.portfoliohub.template.entity.TemplateStatus;
import com.portfoliohub.template.entity.TemplateVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TemplateRepository extends JpaRepository<Template, UUID> {
    boolean existsBySlugIgnoreCase(String slug);
    Optional<Template> findBySlugIgnoreCaseAndStatus(String slug, TemplateStatus status);
    Page<Template> findByStatusAndVisibility(TemplateStatus status, TemplateVisibility visibility, Pageable pageable);
}
