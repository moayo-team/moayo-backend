package com.moayo.moayobackend.profile.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 ProfileUpdateRequest
 - 프로필 수정/저장 요청 DTO
 - 기능명세: bio/university/major는 공백 허용 안 함
*/
public record ProfileUpdateRequest(
        @Schema(description = "이름", example = "배수현")
        @Size(max = 6, message = "이름은 최대 6자입니다.")
        String name,

        @Schema(description = "연락처", example = "01012345678")
        @Size(max = 11, message = "연락처는 최대 11자입니다.")
        String phoneNumber,

        @Schema(description = "대학 명", example = "모아요대학교")
        String university,

        @Schema(description = "수정할 학과/부가정보", example = "컴퓨터학과 (졸업)")
        String major,

        @Schema(description = "수정할 자기소개 (최대 500자)", example = "경력직 같은 신입 개발자입니다.")
        @Size(max = 500, message = "자기소개는 최대 500자입니다.")
        String bio,

        @Schema(description = "수정할 프로필 이미지 URL", example = "https://moayo.com/images/new-profile.png")
        String imageUrl
) {}
