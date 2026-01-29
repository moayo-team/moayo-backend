package com.moayo.moayobackend.experience.controller;

import com.moayo.moayobackend.experience.dto.request.ExperienceAiDraftRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceCreateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceUpdateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceVisibilityRequest;
import com.moayo.moayobackend.experience.dto.response.ExperienceAiDraftResponse;
import com.moayo.moayobackend.experience.dto.response.ExperienceDetailResponse;
import com.moayo.moayobackend.experience.dto.response.ExperienceSummaryResponse;
import com.moayo.moayobackend.experience.service.ExperienceService;
import com.moayo.moayobackend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/experiences")
public class ExperienceController {

    private final ExperienceService experienceService;

    // 1) 내 이력 목록
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ExperienceSummaryResponse>>> myList(
            @AuthenticationPrincipal Long userId
    ) {
        var result = experienceService.listMyExperiences(userId);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "내 이력 목록 조회 성공", result));
    }

    // 2) 내 이력 상세
    @GetMapping("/me/{experienceId}")
    public ResponseEntity<ApiResponse<ExperienceDetailResponse>> myDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId
    ) {
        var result = experienceService.getMyDetail(userId, experienceId);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "내 이력 상세 조회 성공", result));
    }

    // 3) 이력 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ExperienceCreateRequest req
    ) {
        Long id = experienceService.create(userId, req);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "이력 생성 성공", id));
    }

    // 4) 이력 수정
    @PatchMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @RequestBody ExperienceUpdateRequest req
    ) {
        experienceService.update(userId, experienceId, req);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "이력 수정 성공", null));
    }

    // 5) 이력 삭제
    @DeleteMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId
    ) {
        experienceService.delete(userId, experienceId);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "이력 삭제 성공", null));
    }

    // 6) 공개/비공개 변경
    @PatchMapping("/{experienceId}/visibility")
    public ResponseEntity<ApiResponse<Void>> changeVisibility(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @RequestBody ExperienceVisibilityRequest req
    ) {
        experienceService.changeVisibility(userId, experienceId, req);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "공개 여부 변경 성공", null));
    }

    // 7) 타인 공개 이력 보기 (프로필 조회용)
    @GetMapping("/public/{targetUserId}")
    public ResponseEntity<ApiResponse<List<ExperienceSummaryResponse>>> publicList(
            @PathVariable Long targetUserId
    ) {
        var result = experienceService.listPublicByUser(targetUserId);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "공개 이력 조회 성공", result));
    }

    // 8) AI draft
    @PostMapping("/{experienceId}/ai/draft")
    public ResponseEntity<ApiResponse<ExperienceAiDraftResponse>> draftWithAi(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @RequestBody(required = false) ExperienceAiDraftRequest req
    ) {
        var result = experienceService.draftWithAi(userId, experienceId, req);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "AI draft 생성 성공", result));
    }
}
