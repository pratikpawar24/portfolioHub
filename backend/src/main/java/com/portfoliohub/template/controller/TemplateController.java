package com.portfoliohub.template.controller;

import com.portfoliohub.template.dto.TemplateResponse;
import com.portfoliohub.template.dto.TemplateSummaryResponse;
import com.portfoliohub.template.dto.TemplateVersionResponse;
import com.portfoliohub.template.service.TemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {
    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @GetMapping
    public Page<TemplateSummaryResponse> list(
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listPublic(pageable);
    }

    @GetMapping("/{slug}")
    public TemplateResponse get(@PathVariable String slug) {
        return service.getPublic(slug);
    }

    @GetMapping("/versions/{templateVersionId}")
    public TemplateVersionResponse getVersion(@PathVariable UUID templateVersionId) {
        return service.getApprovedVersion(templateVersionId);
    }
}
