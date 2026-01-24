package com.moayo.moayobackend.experience.dto.request;

import java.time.LocalDate;

public record ExperienceUpdateRequest(
        String title,
        String organization,
        LocalDate startDate,
        LocalDate endDate,
        String activity,
        String role,
        String summary
) {}