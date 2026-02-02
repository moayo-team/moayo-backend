package com.moayo.moayobackend.experience.controller;

import com.moayo.moayobackend.experience.dto.request.ExperienceLinkCreateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceLinkUpdateRequest;
import com.moayo.moayobackend.experience.dto.response.ExperienceLinkResponse;
import com.moayo.moayobackend.experience.service.ExperienceLinkService;
import com.moayo.moayobackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "이력서 링크 첨부", description = "이력서에 링크를 첨부/조회/수정/삭제하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experiences/{experienceId}/attachments/links")
public class ExperienceLinkController {

    private final ExperienceLinkService linkService;

    @Operation(summary = "이력서 링크 첨부 추가", description = "이력서 항목에 관련 링크를 추가합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createLink(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @RequestBody ExperienceLinkCreateRequest req
    ) {
        linkService.create(userId, experienceId, req);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "링크 추가 성공", null)
        );
    }

    @Operation(summary = "이력서 링크 첨부 목록 조회", description = "특정 이력서에 첨부된 링크 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExperienceLinkResponse>>> listLinks(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId
    ) {
        var result = linkService.list(userId, experienceId);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "링크 목록 조회 성공", result)
        );
    }

    @Operation(summary = "이력서 첨부 링크 수정", description = "이력서에 첨부된 링크 정보를 수정합니다.")
    @PatchMapping("/{linkId}")
    public ResponseEntity<ApiResponse<Void>> updateLink(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @PathVariable Long linkId,
            @RequestBody ExperienceLinkUpdateRequest req
    ) {
        linkService.update(userId, experienceId, linkId, req);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "링크 수정 성공", null)
        );
    }

    @Operation(summary = "이력서 첨부 링크 삭제", description = "이력서에 첨부된 링크를 삭제합니다.")
    @DeleteMapping("/{linkId}")
    public ResponseEntity<ApiResponse<Void>> deleteLink(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @PathVariable Long linkId
    ) {
        linkService.delete(userId, experienceId, linkId);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "링크 삭제 성공", null)
        );
    }
}
