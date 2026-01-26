package com.moayo.moayobackend.profile.dto.request;

import java.util.List;

public record UserInterestTagUpdateRequest(
        List<Long> tagIds
) {}
