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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "이력서(Experience)", description = "이력서(Experience) 생성, 조회, 수정, 삭제 및 AI 초안 생성 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experiences")
public class ExperienceController {

    private final ExperienceService experienceService;

    @Operation(summary = "내 이력서 목록 조회", description = "로그인한 사용자의 전체 이력서 목록을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ExperienceSummaryResponse>>> myList(
            @AuthenticationPrincipal Long userId
    ) {
        var result = experienceService.listMyExperiences(userId);

        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "내 이력 목록 조회 성공", result)
        );
    }

    @Operation(summary = "내 이력서 상세 조회", description = "로그인한 사용자의 특정 이력서 상세 정보를 조회합니다.")
    @GetMapping("/me/{experienceId}")
    public ResponseEntity<ApiResponse<ExperienceDetailResponse>> myDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId
    ) {
        var result = experienceService.getMyDetail(userId, experienceId);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "내 이력 상세 조회 성공", result)
        );
    }

    @Operation(summary = "이력서 생성", description = "로그인한 사용자의 이력서 항목을 새로 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ExperienceCreateRequest req
    ) {
        Long id = experienceService.create(userId, req);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "이력 생성 성공", id)
        );
    }

    @Operation(summary = "이력서 수정", description = "로그인한 사용자가 본인의 이력서 항목을 수정합니다. ")
    @PatchMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @RequestBody ExperienceUpdateRequest req
    ) {
        experienceService.update(userId, experienceId, req);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "이력 수정 성공", null)
        );
    }

    @Operation(summary = "이력서 삭제", description = "로그인한 사용자가 본인의 이력서 항목을 삭제합니다.")
    @DeleteMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId
    ) {
        experienceService.delete(userId, experienceId);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "이력 삭제 성공", null));
    }

    @Operation(summary = "이력서 공개 여부 변경", description = "이력서 항목의 공개/비공개 상태를 변경합니다.")
    @PatchMapping("/{experienceId}/visibility")
    public ResponseEntity<ApiResponse<Void>> changeVisibility(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @RequestBody ExperienceVisibilityRequest req
    ) {
        experienceService.changeVisibility(userId, experienceId, req);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "공개 여부 변경 성공", null)
        );
    }

    @Operation(summary = "공개 이력서 상세 조회", description = "공개(visible=true) 상태인 이력서 항목의 상세 정보를 조회합니다.")
    @GetMapping("/public/{experienceId}")
    public ResponseEntity<ApiResponse<ExperienceDetailResponse>> publicDetail(
            @PathVariable Long experienceId
    ) {
        var result = experienceService.getPublicDetail(experienceId);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "공개 이력 상세 조회 성공", result)
        );
    }

    @Operation(
            summary = "AI 기반 활동 소개(summary) 생성",
            description = "사용자가 입력한 팩트 정보와 줄글을 바탕으로 활동 소개(summary)를 AI가 생성합니다. (저장 X)"
    )
    @PostMapping("/ai/summary")
    public ResponseEntity<ApiResponse<ExperienceAiDraftResponse>> aiSummary(
            @RequestBody ExperienceAiDraftRequest req
    ) {
        var result = experienceService.draftWithAi(req);

        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "AI 활동 소개 생성 성공", result)
        );
    }
}
