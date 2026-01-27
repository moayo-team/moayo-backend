package com.moayo.moayobackend.profile.dto.response;

/*
 UserInterestTagResponse
 - 기존 구조를 유지하기 위해 남겨둔 응답 DTO
 - 프론트가 매핑 id까지 필요 없다면 제거하고 InterestTagResponse 리스트로 통일해도 됨
*/
public record UserInterestTagResponse(Long id, Long tagId, String tagName) {
}
