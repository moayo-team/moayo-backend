package com.moayo.moayobackend.profile.dto.request;

public record ProfileCreateRequest(
        String imageUrl,
        String bio,
        String university,
        String major
) {}
