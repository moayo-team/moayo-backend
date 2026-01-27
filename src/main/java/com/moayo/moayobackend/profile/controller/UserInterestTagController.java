package com.moayo.moayobackend.profile.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.profile.dto.request.UserInterestTagUpdateRequest;
import com.moayo.moayobackend.profile.service.UserInterestTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/*
 UserInterestTagController
 - 내 관심태그 조회/저장
 - 저장은 "전체 교체" 방식 (태그 개수 제한 없음)
*/

@Tag(name="내 관심태그 조회/저장", description = "내가 저장한 관심태그 조회/저장")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/interest-tags")
public class UserInterestTagController {

    private final UserInterestTagService userInterestTagService;

    @Operation(summary = "내 관심 태그 조회", description = "내가 저장했던 관심 태그 목록 조회")
    @GetMapping
    public ApiResponse<?> getMine(@AuthenticationPrincipal Long userId) {
//        Long targetId = (userId != null) ? userId : 1L;
        return ApiResponse.ok("SUCCESS", "내 관심 태그 조회에 성공했습니다.", userInterestTagService.findMine(userId));
    }

    @Operation(summary = "내 관심 태그 수정", description = "내가 저장했던 관심 태그 목록 수정")
    @PutMapping
    public ApiResponse<?> replace(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserInterestTagUpdateRequest req
    ) {
//        Long targetId = (userId != null) ? userId : 1L;
        System.out.println(">>> 현재 로그인된 유저 ID: " + userId);

        userInterestTagService.replace(userId, req.tagIds());
        return ApiResponse.ok("SUCCESS", "관심 태그가 성공적으로 변경되었습니다.", null);
    }

}
