package com.moayo.moayobackend.profile.dto.response;

public record ProfileUserResponse(
        Long id,
        Long userId,
        String imageUrl,
        String bio,
        String university,
        String major
) {}
