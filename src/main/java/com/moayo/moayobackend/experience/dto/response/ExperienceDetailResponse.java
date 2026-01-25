package com.moayo.moayobackend.experience.dto.response;

import java.time.LocalDate;

public record ExperienceDetailResponse(
        Long experienceId,
        String title,
        String organization,
        LocalDate startDate,
        LocalDate endDate,
        String activity,
        String role,
        String summary,
        Boolean visible
) {}
