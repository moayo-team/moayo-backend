package com.moayo.moayobackend.experience.controller;

import com.moayo.moayobackend.experience.dto.response.ExperienceSummaryResponse;
import com.moayo.moayobackend.experience.service.ExperienceService;
import com.moayo.moayobackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "공개 이력서 조회", description = "특정 사용자의 공개 이력서 목록 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserPublicExperienceController {

    private final ExperienceService experienceService;

    @Operation(summary = "특정 사용자의 공개 이력서 목록 조회", description = "특정 사용자가 공개 설정한 이력서 목록(visible=true)을 조회합니다.")
    @GetMapping("/{userId}/experiences")
    public ResponseEntity<ApiResponse<List<ExperienceSummaryResponse>>> publicList(
            @PathVariable Long userId
    ) {
        var result = experienceService.listPublicByUser(userId);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "공개 이력 조회 성공", result)
        );
    }
}
