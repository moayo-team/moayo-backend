package com.moayo.moayobackend.profile.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.profile.service.InterestTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/*
 InterestTagController
 - 관심태그 전체 목록 조회
 - 이 API가 공개인지 여부는 SecurityConfig에서 결정 (여기서는 수정하지 않음)
*/


@Tag(name = "관심태그 조회", description = "모아요 전체 관심태그 목록 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/interest-tags")
public class InterestTagController {

    private final InterestTagService interestTagService;

    @Operation(summary = "관심태그 조회", description = "모아요 전체 관심태그 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<?> getAll() {
        return ApiResponse.ok("SUCCESS", "관심 태그 목록 조회에 성공했습니다.", interestTagService.findAll());
    }
}
