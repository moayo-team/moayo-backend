package com.moayo.moayobackend.profile.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/*
 ProfileIndexItemUpdateRequest
 - 프로필 기본정보 추가 항목 수정 요청 DTO
*/
public record ProfileIndexItemUpdateRequest(
        @Schema(description = "수정할 항목 이름 (최대 10자)", example = "GitHub")
        @Size(max = 20, message = "indexKey는 최대 20자입니다.")
        String indexKey,

        @Schema(description = "수정할 항목 내용 (최대 20자)", example = "moayo-git")
        @Size(max = 20, message = "indexValue는 최대 20자입니다.")
        String indexValue,

        @Schema(description = "수정할 텍스트 값 (최대 10자)", example = "저장소")
        @Size(max = 10, message = "textValue는 최대 10자입니다.")
        String textValue,

        @Schema(description = "수정할 url (최대 500자)", example =  "https://blog.naver.com/moayo")
        @Size(max = 500, message = "url이 너무 깁니다.")
        String linkUrl
) {}
