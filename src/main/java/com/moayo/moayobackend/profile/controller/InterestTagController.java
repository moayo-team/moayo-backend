package com.moayo.moayobackend.profile.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.profile.service.InterestTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/interest-tags")
public class InterestTagController {

    private final InterestTagService interestTagService;

    @GetMapping
    public ApiResponse<?> getAllInterestTags() {
        return ApiResponse.ok(
                "SUCCESS",
                "관심 태그 목록 조회에 성공했습니다.",
                interestTagService.findAll()
        );
    }
}
