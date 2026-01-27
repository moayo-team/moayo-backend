package com.moayo.moayobackend.profile.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 ProfileCreateRequest
 - 프로필 최초 생성 요청 DTO
 - 프로필 화면의 저장 버튼을 "최초 생성"으로 처리할 경우 사용
*/
public record ProfileCreateRequest(
        @Schema(description = "이름", example = "홍길동")
        @Size(max = 6, message = "이름은 최대 6자입니다.")
        String name,

        @Schema(description = "연락처(숫자만)", example = "01012345678")
        @Size(max = 11, message = "연락처는 최대 11자입니다.")
        String phoneNumber,

        @Schema(description = "대학 명", example = "모아요대학교")
        @NotBlank(message = "대학은 필수입니다.")
        String university,

        @Schema(description = "학과", example = "컴퓨터공학과")
        @NotBlank(message = "학과는 필수입니다.")
        String major,

        @Schema(description = "자기소개 (최대 500자)", example = "반갑습니다. 모아요 백엔드 개발자입니다.")
        @NotBlank(message = "자기소개는 필수입니다.")
        @Size(max = 500, message = "자기소개는 최대 500자입니다.")
        String bio,

        @Schema(description = "프로필 사진 URL", example = "https://moayo.com/images/profile1.png")
        String imageUrl
) {}
