package com.moayo.moayobackend.profile.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.profile.dto.request.UserInterestTagUpdateRequest;
import com.moayo.moayobackend.profile.service.UserInterestTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/interest-tags")
public class UserInterestTagController {

    private final UserInterestTagService userInterestTagService;

    @GetMapping
    public ApiResponse<?> getMyInterestTags(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(
                "SUCCESS",
                "내 관심 태그 조회에 성공했습니다.",
                userInterestTagService.findMine(userId)
        );
    }

    @PutMapping
    public ApiResponse<?> replaceMyInterestTags(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserInterestTagUpdateRequest req
    ) {
        userInterestTagService.replace(userId, req.tagIds());
        return ApiResponse.ok(
                "SUCCESS",
                "관심 태그가 성공적으로 변경되었습니다.",
                null
        );
    }
}
