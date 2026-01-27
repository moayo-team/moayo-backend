package com.moayo.moayobackend.user.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.user.entity.User;
import com.moayo.moayobackend.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * API : GET /api/v1/users/me
 * Authorization: Bearer accessToken 필요
 */

@Tag(name = "사용자 정보", description = "사용자 계정 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    @Operation(summary = "내 계정 정보 조회", description = "현재 로그인한 사용자의 기본 계정 정보(ID, 이메일, 이름, 전화번호)를 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<MeResponse> me(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ApiResponse.fail("AUTH-401", "인증이 필요합니다.");
        }

        Long userId = (Long) authentication.getPrincipal();
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) return ApiResponse.fail("USER-404", "유저가 없어.");

        return ApiResponse.ok("SUCCESS-200", "요청에 성공했습니다.",
                new MeResponse(user.getId(), user.getEmail(), user.getName(), user.getPhoneNumber()));
    }

    public record MeResponse(
            @Schema(description = "유저 고유 ID", example = "1")
            Long id,
            @Schema(description = "계정 이메일", example = "moayo@example.com")
            String email,
            @Schema(description = "사용자 이름", example = "홍길동")
            String name,
            @Schema(description = "전화번호", example = "010-1234-5678")
            String phoneNumber
    ) {}
}
