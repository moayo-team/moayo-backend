package com.moayo.moayobackend.profile.dto.request;

import com.moayo.moayobackend.profile.entity.ProfileIndexItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
 ProfileIndexItemCreateRequest
 - 프로필 기본정보 추가 항목 생성 요청 DTO
*/
@Schema(description = "프로필 기본정보 추가 항목 생성 요청")
public record ProfileIndexItemCreateRequest(
        @Schema(description = "항목 제목 (왼쪽 영역, 최대 10자)", example = "MBTI")
        @NotBlank(message = "indexKey는 필수입니다.")
        @Size(max = 20, message = "indexKey는 최대 20자입니다.")
        String indexKey,

        @Schema(description = "항목 내용 (오른쪽 영역, 최대 20자)", example = "ENTP")
//        @NotBlank(message = "indexValue는 필수입니다.")
        @Size(max = 20, message = "indexValue는 최대 20자입니다.")
        String indexValue,

        @Schema(description = "항목 타입 (text, link, file)", example = "TEXT")
        @NotNull(message = "itemType은 필수입니다.")
        ProfileIndexItem.ItemType itemType,

        @Schema(description = "추가 텍스트 값 (최대 10자)", example = "활동가")
        @Size(max = 10, message = "textValue는 최대 10자입니다.")
        String textValue,

        @Schema(description = "연결할 URL (최대 500자/링크&파일 타입)", example = "https://blog.naver.com/moayo")
        @Size(max = 500, message = "linkUrl이 너무 깁니다.")
        String linkUrl
) {}
