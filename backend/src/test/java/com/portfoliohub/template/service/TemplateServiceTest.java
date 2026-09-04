package com.portfoliohub.template.service;

import com.portfoliohub.auth.repository.UserRepository;
import com.portfoliohub.template.entity.Template;
import com.portfoliohub.template.entity.TemplateVersion;
import com.portfoliohub.template.entity.TemplateStatus;
import com.portfoliohub.template.entity.TemplateVersionStatus;
import com.portfoliohub.template.entity.TemplateVisibility;
import com.portfoliohub.template.repository.TemplateRepository;
import com.portfoliohub.template.repository.TemplateVersionRepository;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TemplateServiceTest {
    private final TemplateRepository templates = mock(TemplateRepository.class);
    private final TemplateVersionRepository versions = mock(TemplateVersionRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final TemplateService service = new TemplateService(templates, versions, users);
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void acceptsPortfolioSchemaWithinWildcardTemplateRange() {
        Template template = template();
        TemplateVersion version = version(template, "1.2.0", "1.0", "1.x");
        UUID versionId = UUID.randomUUID();
        when(versions.findByIdAndStatus(versionId, TemplateVersionStatus.APPROVED)).thenReturn(java.util.Optional.of(version));

        TemplateVersion resolved = service.requireCompatibleApprovedVersion(versionId, "1.8");

        assertThat(resolved).isSameAs(version);
    }

    @Test
    void rejectsPortfolioSchemaOutsideTemplateRange() {
        Template template = template();
        TemplateVersion version = version(template, "1.2.0", "1.0", "1.4");
        UUID versionId = UUID.randomUUID();
        when(versions.findByIdAndStatus(versionId, TemplateVersionStatus.APPROVED)).thenReturn(java.util.Optional.of(version));

        assertThatThrownBy(() -> service.requireCompatibleApprovedVersion(versionId, "1.5"))
                .isInstanceOf(com.portfoliohub.common.api.ApiException.class)
                .hasMessageContaining("not compatible");
    }

    private Template template() {
        Template template = new Template();
        template.setStatus(TemplateStatus.ACTIVE);
        template.setVisibility(TemplateVisibility.PUBLIC);
        return template;
    }

    private TemplateVersion version(Template template, String version, String min, String max) {
        TemplateVersion value = new TemplateVersion();
        value.setTemplate(template);
        value.setVersion(version);
        value.setSchemaMin(min);
        value.setSchemaMax(max);
        value.setStatus(TemplateVersionStatus.APPROVED);
        try {
            value.setManifest(jsonMapper.readTree("{\"manifestVersion\":\"1.0\"}"));
        } catch (Exception ignored) {
            // Manifest is not part of compatibility behavior under test.
        }
        return value;
    }
}
