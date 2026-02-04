package com.moayo.moayobackend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedUserDto {
    @Schema(description = "추천된 유저의 ID", example = "1")
    private Long userId;

    @Schema(description = "추천된 유저의 이름", example = "배수현")
    private String name;

    private String imageUrl;
    private String bio;

    @Schema(description = "AI 또는 로직에 의해 생성된 맞춤형 추천 사유")
    private String matchReason;
}
