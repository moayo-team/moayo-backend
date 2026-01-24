package com.moayo.moayobackend.experience.dto.response;

import java.time.LocalDate;

public record ExperienceSummaryResponse(
        Long experienceId,
        String organization,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String activity,
        String role,
        Boolean visible
) {}
