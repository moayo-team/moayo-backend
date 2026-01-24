package com.moayo.moayobackend.experience.dto.response;

import java.time.LocalDate;

public record ExperienceAiDraftResponse(
        String organization,
        String title,
        String activity,
        String role,
        String summary,
        LocalDate startDate,
        LocalDate endDate
) {}
