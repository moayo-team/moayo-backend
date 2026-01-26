package com.moayo.moayobackend.experience.controller;

import com.moayo.moayobackend.experience.dto.request.ExperienceLinkCreateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceLinkUpdateRequest;
import com.moayo.moayobackend.experience.dto.response.ExperienceLinkResponse;
import com.moayo.moayobackend.experience.service.ExperienceLinkService;
import com.moayo.moayobackend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/experiences/{experienceId}/attachments/links")
public class ExperienceLinkController {

    private final ExperienceLinkService linkService;

    // 링크 추가
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createLink(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @RequestBody ExperienceLinkCreateRequest req
    ) {
        linkService.create(userId, experienceId, req);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "링크 추가 성공", null));
    }

    // 링크 목록
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExperienceLinkResponse>>> listLinks(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId
    ) {
        var result = linkService.list(userId, experienceId);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "링크 목록 조회 성공", result));
    }

    // 링크 수정
    @PatchMapping("/{linkId}")
    public ResponseEntity<ApiResponse<Void>> updateLink(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @PathVariable Long linkId,
            @RequestBody ExperienceLinkUpdateRequest req
    ) {
        linkService.update(userId, experienceId, linkId, req);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "링크 수정 성공", null));
    }

    // 링크 삭제
    @DeleteMapping("/{linkId}")
    public ResponseEntity<ApiResponse<Void>> deleteLink(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @PathVariable Long linkId
    ) {
        linkService.delete(userId, experienceId, linkId);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "링크 삭제 성공", null));
    }
}
