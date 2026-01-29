package com.moayo.moayobackend.profile.dto.response;

import java.util.List;

/*
 ProfileMeResponse
 - 프로필 화면 진입에 필요한 데이터를 한 번에 내려주는 응답 DTO
 - 기능명세서가 요구하는 항목(유저+프로필+태그+추가항목+학력첨부)을 통합
*/
public record ProfileMeResponse(
        UserPart user,
        ProfilePart profile,
        List<InterestTagResponse> interestTags,
        List<ProfileIndexItemResponse> indexItems,
        List<ProfileDocumentResponse> documents
) {
    public record UserPart(Long id, String name, String email, String phoneNumber) {}
    public record ProfilePart(Long id, String imageUrl, String university, String major, String bio) {}
}
