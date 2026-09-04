package com.portfoliohub.template.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SelectTemplateRequest(@NotNull UUID templateVersionId) {}
