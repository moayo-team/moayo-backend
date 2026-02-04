package com.moayo.moayobackend.home.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.home.dto.HomeResponseDto;
import com.moayo.moayobackend.home.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "홈", description = "홈 화면 데이터 조회 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeController {

    private final HomeService homeService;

    @Operation(
            summary = "홈 화면 조회",
            description = "안읽은 쪽지 상태, 마감 임박 게시글, AI 추천 유저 정보를 조회합니다."
    )
    @GetMapping
    public ApiResponse<HomeResponseDto> getHome(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "3") int postsLimit,
            @RequestParam(defaultValue = "4") int recoLimit,
            @RequestParam(defaultValue = "similar") String recoType
    ) {
        HomeResponseDto result =
                homeService.loadHome(userId, postsLimit, recoLimit, recoType);

        return ApiResponse.ok(
                "SUCCESS",
                "홈 화면 조회에 성공했습니다.",
                result
        );
    }
}
