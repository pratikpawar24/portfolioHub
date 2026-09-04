package com.portfoliohub.creator.service;

import com.portfoliohub.auth.entity.User;
import com.portfoliohub.auth.repository.UserRepository;
import com.portfoliohub.common.api.ApiException;
import com.portfoliohub.creator.dto.CreatorProfileRequest;
import com.portfoliohub.creator.dto.CreatorProfileResponse;
import com.portfoliohub.creator.entity.CreatorProfile;
import com.portfoliohub.creator.repository.CreatorProfileRepository;
import com.portfoliohub.template.entity.TemplateStatus;
import com.portfoliohub.template.repository.TemplateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreatorProfileService {
    private final CreatorProfileRepository profiles;
    private final UserRepository users;
    private final TemplateRepository templates;

    public CreatorProfileService(CreatorProfileRepository profiles, UserRepository users, TemplateRepository templates) {
        this.profiles = profiles;
        this.users = users;
        this.templates = templates;
    }

    @Transactional
    public CreatorProfileResponse upsert(UUID userId, CreatorProfileRequest request) {
        User user = user(userId);
        CreatorProfile profile = profiles.findByUserId(userId).orElseGet(() -> {
            CreatorProfile created = new CreatorProfile();
            created.setUser(user);
            return created;
        });
        profile.setBio(request.bio());
        profile.setAvatarUrl(request.avatarUrl());
        profile.setWebsiteUrl(request.websiteUrl());
        return response(profiles.save(profile));
    }

    @Transactional(readOnly = true)
    public CreatorProfileResponse getByUsername(String username) {
        User user = users.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> notFound("CREATOR_NOT_FOUND", "Creator was not found"));
        CreatorProfile profile = profiles.findByUserId(user.getId()).orElse(null);
        long templateCount = templates.countByCreatorIdAndStatus(user.getId(), TemplateStatus.ACTIVE);
        return new CreatorProfileResponse(
                user.getId(), user.getUsername(), user.getDisplayName(),
                profile == null ? null : profile.getBio(),
                profile == null ? null : profile.getAvatarUrl(),
                profile == null ? null : profile.getWebsiteUrl(),
                templateCount,
                profile == null ? user.getUpdatedAt() : profile.getUpdatedAt());
    }

    private CreatorProfileResponse response(CreatorProfile profile) {
        User user = profile.getUser();
        long templateCount = templates.countByCreatorIdAndStatus(user.getId(), TemplateStatus.ACTIVE);
        return new CreatorProfileResponse(user.getId(), user.getUsername(), user.getDisplayName(),
                profile.getBio(), profile.getAvatarUrl(), profile.getWebsiteUrl(), templateCount, profile.getUpdatedAt());
    }

    private User user(UUID id) {
        return users.findById(id).orElseThrow(() -> notFound("USER_NOT_FOUND", "User was not found"));
    }

    private ApiException notFound(String code, String message) {
        return new ApiException(HttpStatus.NOT_FOUND, code, message);
    }
}
