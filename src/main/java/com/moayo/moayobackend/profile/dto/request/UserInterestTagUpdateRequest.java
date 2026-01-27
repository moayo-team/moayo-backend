package com.moayo.moayobackend.profile.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/*
 UserInterestTagUpdateRequest
 - 유저 관심태그를 "전체 교체" 방식으로 저장하는 요청 DTO
 - 태그 개수 제한 없음
*/
public record UserInterestTagUpdateRequest(
        @Schema(description = "변경할 태그 ID 리스트 (id: 1~8) (개수 제한 없음)", example = "[1, 2, 3]")
        @NotNull(message = "tagIds는 필수입니다.")
        List<Long> tagIds
) {}
