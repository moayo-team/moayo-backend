package com.moayo.moayobackend.profile.dto.response;

import java.util.List;

/*
 ProfileUserResponse
 - 타인 프로필 조회 응답 DTO
 - 공개 범위 정책에 따라 documents는 제외(필요 시 포함 가능)
*/
public record ProfileUserResponse(
        Long userId,
        String name,
        String email,
        String phoneNumber,
        String imageUrl,
        String university,
        String major,
        String bio,
        List<InterestTagResponse> interestTags,
        List<ProfileIndexItemResponse> indexItems
) {}
