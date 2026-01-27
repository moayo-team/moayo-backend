package com.moayo.moayobackend.profile.dto.response;

import com.moayo.moayobackend.profile.entity.InterestTag;

/*
 InterestTagResponse
 - 관심태그 마스터 목록 응답 DTO
*/
public record InterestTagResponse(Long id, String name) {
    public static InterestTagResponse from(InterestTag t) {
        return new InterestTagResponse(t.getId(), t.getName());
    }
}
