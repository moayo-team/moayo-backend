package com.moayo.moayobackend.profile.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.profile.dto.request.ProfileCreateRequest;
import com.moayo.moayobackend.profile.dto.request.ProfileUpdateRequest;
import com.moayo.moayobackend.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/*
 ProfileController
 - 내 프로필 화면 조회/생성/수정
 - 타인 프로필 조회
 - 인증이 필요한 API는 @AuthenticationPrincipal Long userId 사용
*/

@Tag(name = "프로필 관리", description = "내 프로필 조회, 생성, 수정 및 타인 프로필 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 프로필 기본 정보를 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<?> getMe(@AuthenticationPrincipal Long userId) {
//        Long targetId = (userId != null) ? userId : 1L;
        return ApiResponse.ok("SUCCESS", "내 프로필 조회에 성공했습니다.", profileService.getMe(userId));
    }

    @Operation(summary = "내 프로필 생성", description = "내 프로필 정보를 등록합니다.")
    @PostMapping("/me")
    public ApiResponse<?> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProfileCreateRequest req
    ) {
//        Long targetId = (userId != null) ? userId : 1L;
        profileService.create(userId, req);
        return ApiResponse.ok("SUCCESS", "프로필 생성에 성공했습니다.", null);
    }

    @Operation(summary = "내 프로필 수정", description = "기존 프로필 정보를 수정(PATCH)합니다.")
    @PatchMapping("/me")
    public ApiResponse<?> update(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ProfileUpdateRequest req
    ) {
//        Long targetId = (userId != null) ? userId : 1L;
        profileService.update(userId, req);
        return ApiResponse.ok("SUCCESS", "프로필 수정에 성공했습니다.", null);
    }

    @Operation(summary = "타인 프로필 조회", description = "다른 사용자의 프로필을 조회합니다.")
    @GetMapping("/{userId}")
    public ApiResponse<?> getUser(@PathVariable Long userId) {
//        Long targetId = (userId != null) ? userId : 1L;
        return ApiResponse.ok("SUCCESS", "타인 프로필 조회에 성공했습니다.", profileService.getUser(userId));
    }
}
