package com.moayo.moayobackend.profile.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.profile.dto.request.ProfileCreateRequest;
import com.moayo.moayobackend.profile.dto.request.ProfileUpdateRequest;
import com.moayo.moayobackend.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ApiResponse<?> getMyProfile(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(
                "SUCCESS",
                "내 프로필 조회에 성공했습니다.",
                profileService.getMyProfile(userId)
        );
    }

    @PostMapping("/me")
    public ApiResponse<?> createMyProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody ProfileCreateRequest req
    ) {
        profileService.create(userId, req);
        return ApiResponse.ok(
                "SUCCESS",
                "프로필 생성에 성공했습니다.",
                null
        );
    }

    @PatchMapping("/me")
    public ApiResponse<?> updateMyProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody ProfileUpdateRequest req
    ) {
        profileService.update(userId, req);
        return ApiResponse.ok(
                "SUCCESS",
                "프로필 수정에 성공했습니다.",
                null
        );
    }

    @GetMapping("/{userId}")
    public ApiResponse<?> getOtherProfile(@PathVariable Long userId) {
        return ApiResponse.ok(
                "SUCCESS",
                "프로필 조회에 성공했습니다.",
                profileService.getOtherProfile(userId)
        );
    }
}
