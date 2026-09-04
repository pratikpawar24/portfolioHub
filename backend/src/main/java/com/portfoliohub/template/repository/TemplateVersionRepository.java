package com.portfoliohub.template.repository;

import com.portfoliohub.template.entity.TemplateVersion;
import com.portfoliohub.template.entity.TemplateVersionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, UUID> {
    Optional<TemplateVersion> findByIdAndStatus(UUID id, TemplateVersionStatus status);
    Optional<TemplateVersion> findByTemplateIdAndVersion(UUID templateId, String version);
    java.util.List<TemplateVersion> findByTemplateIdOrderByVersionDesc(UUID templateId);
}
