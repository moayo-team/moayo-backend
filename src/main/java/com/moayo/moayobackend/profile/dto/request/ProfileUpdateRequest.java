package com.moayo.moayobackend.profile.dto.request;

public record ProfileUpdateRequest(
        String imageUrl,
        String bio,
        String university,
        String major
) {}
