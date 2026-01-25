package com.moayo.moayobackend.user.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.user.entity.User;
import com.moayo.moayobackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * API : GET /api/v1/users/me
 * Authorization: Bearer accessToken 필요
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ApiResponse<MeResponse> me(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ApiResponse.fail("AUTH-401", "인증이 필요해.");
        }

        Long userId = (Long) authentication.getPrincipal();
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) return ApiResponse.fail("USER-404", "유저가 없어.");

        return ApiResponse.ok("SUCCESS-200", "요청에 성공했습니다.",
                new MeResponse(user.getId(), user.getEmail(), user.getName(), user.getPhoneNumber()));
    }

    public record MeResponse(Long id, String email, String name, String phoneNumber) {}
}
