package com.moayo.moayobackend.user.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.user.dto.UserRecommendationResponseDto;
import com.moayo.moayobackend.user.service.UserRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저 추천", description = "관심태그/이력 기반 AI 추천")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/recommendations")
public class UserRecommendationController {

    private final UserRecommendationService recommendationService;

    @Operation(summary = "AI 추천 유저 조회", description = "관심태그 + 이력(Experience) 기반으로 유사/시너지 추천 유저를 조회합니다.")
    @GetMapping
    public ApiResponse<UserRecommendationResponseDto> recommend(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "similar") String type,
            @RequestParam(defaultValue = "4") int limit
    ) {
        return ApiResponse.ok(
                "SUCCESS",
                "추천 유저 조회에 성공했습니다.",
                recommendationService.recommend(userId, type, limit)
        );
    }
}
