package com.portfoliohub.template.controller;

import com.portfoliohub.template.dto.RegisterTemplateRequest;
import com.portfoliohub.template.dto.TemplateResponse;
import com.portfoliohub.template.service.TemplateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/templates")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTemplateController {
    private final TemplateService service;

    public AdminTemplateController(TemplateService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TemplateResponse register(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RegisterTemplateRequest request) {
        return service.registerFirstParty(UUID.fromString(jwt.getSubject()), request);
    }
}
