package com.portfoliohub.template.repository;

import com.portfoliohub.template.entity.TemplateVersion;
import com.portfoliohub.template.entity.TemplateVersionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, UUID> {
    Optional<TemplateVersion> findByIdAndStatus(UUID id, TemplateVersionStatus status);
    Optional<TemplateVersion> findByTemplateIdAndVersion(UUID templateId, String version);
    List<TemplateVersion> findByTemplateIdOrderByVersionDesc(UUID templateId);

    @Query("select v.id from TemplateVersion v where v.template.id = :templateId")
    List<UUID> findIdsByTemplateId(@Param("templateId") UUID templateId);
}
