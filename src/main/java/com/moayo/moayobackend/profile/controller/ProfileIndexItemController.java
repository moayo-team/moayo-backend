package com.moayo.moayobackend.profile.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.profile.dto.request.ProfileIndexItemCreateRequest;
import com.moayo.moayobackend.profile.dto.request.ProfileIndexItemUpdateRequest;
import com.moayo.moayobackend.profile.service.ProfileIndexItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles/me/index-items")
public class ProfileIndexItemController {

    private final ProfileIndexItemService profileIndexItemService;

    @PostMapping
    public ApiResponse<?> createIndexItem(
            @AuthenticationPrincipal Long userId,
            @RequestBody ProfileIndexItemCreateRequest req
    ) {
        profileIndexItemService.create(userId, req);
        return ApiResponse.ok(
                "SUCCESS",
                "프로필 추가 항목이 생성되었습니다.",
                null
        );
    }

    @GetMapping
    public ApiResponse<?> getMyIndexItems(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(
                "SUCCESS",
                "프로필 추가 항목 조회에 성공했습니다.",
                profileIndexItemService.findMine(userId)
        );
    }

    @PatchMapping("/{itemId}")
    public ApiResponse<?> updateIndexItem(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long itemId,
            @RequestBody ProfileIndexItemUpdateRequest req
    ) {
        profileIndexItemService.update(userId, itemId, req);
        return ApiResponse.ok(
                "SUCCESS",
                "프로필 추가 항목이 수정되었습니다.",
                null
        );
    }

    @DeleteMapping("/{itemId}")
    public ApiResponse<?> deleteIndexItem(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long itemId
    ) {
        profileIndexItemService.delete(userId, itemId);
        return ApiResponse.ok(
                "SUCCESS",
                "프로필 추가 항목이 삭제되었습니다.",
                null
        );
    }
}
