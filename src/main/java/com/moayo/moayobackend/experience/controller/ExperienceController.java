package com.moayo.moayobackend.experience.controller;

import com.moayo.moayobackend.experience.dto.request.ExperienceAiDraftRequest;
import com.moayo.moayobackend.experience.dto.response.ExperienceAiDraftResponse;
import com.moayo.moayobackend.experience.service.ExperienceService;
import com.moayo.moayobackend.experience.dto.request.ExperienceCreateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceUpdateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceVisibilityRequest;
import com.moayo.moayobackend.experience.dto.response.ExperienceDetailResponse;
import com.moayo.moayobackend.experience.dto.response.ExperienceSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ExperienceController {

    private final ExperienceService experienceService;

    // 내 이력 목록 조회 (카드 리스트)
    @GetMapping("/experiences")
    public ResponseEntity<List<ExperienceSummaryResponse>> listMyExperiences(
            @RequestHeader("X-MEMBER-ID") Long memberId
    ) {
        return ResponseEntity.ok(experienceService.listMyExperiences(memberId));
    }

    // 이력 생성 (이력 추가 페이지 등록하기)
    @PostMapping("/experiences")
    public ResponseEntity<Void> create(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @RequestBody ExperienceCreateRequest req
    ) {
        Long id = experienceService.create(memberId, req);
        return ResponseEntity.created(URI.create("/api/v1/experiences/" + id)).build();
    }

    // 이력 상세 조회 (카드 클릭 -> 팝업 열기)
    @GetMapping("/experiences/{experienceId}")
    public ResponseEntity<ExperienceDetailResponse> getDetail(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId
    ) {
        return ResponseEntity.ok(experienceService.getMyDetail(memberId, experienceId));
    }

    // 이력 수정 저장 (수정 페이지 저장하기)
    @PatchMapping("/experiences/{experienceId}")
    public ResponseEntity<Void> update(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId,
            @RequestBody ExperienceUpdateRequest req
    ) {
        experienceService.update(memberId, experienceId, req);
        return ResponseEntity.noContent().build();
    }

    // 이력 삭제 (팝업에서 삭제하기)
    @DeleteMapping("/experiences/{experienceId}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId
    ) {
        experienceService.delete(memberId, experienceId);
        return ResponseEntity.noContent().build();
    }

    // 이력 공개/비공개 토글
    @PatchMapping("/experiences/{experienceId}/visibility")
    public ResponseEntity<Void> changeVisibility(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId,
            @RequestBody ExperienceVisibilityRequest req
    ) {
        experienceService.changeVisibility(memberId, experienceId, req);
        return ResponseEntity.noContent().build();
    }

    // 타인 프로필: 공개 이력만 조회
    @GetMapping("/users/{userId}/experiences")
    public ResponseEntity<List<ExperienceSummaryResponse>> listPublicByUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(experienceService.listPublicByUser(userId));
    }

    @PostMapping("/{experienceId}/ai/draft")
    public ResponseEntity<ExperienceAiDraftResponse> draftWithAi(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId,
            @RequestBody(required = false) ExperienceAiDraftRequest req
    ) {
        return ResponseEntity.ok(experienceService.draftWithAi(memberId, experienceId, req));
    }
}
