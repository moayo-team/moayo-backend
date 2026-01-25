package com.moayo.moayobackend.experience.controller;

import com.moayo.moayobackend.experience.service.ExperienceLinkService;
import com.moayo.moayobackend.experience.dto.request.ExperienceLinkCreateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceLinkUpdateRequest;
import com.moayo.moayobackend.experience.dto.response.ExperienceLinkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experiences/{experienceId}/attachments/links")
public class ExperienceLinkController {

    private final ExperienceLinkService linkService;

    // 링크 추가
    @PostMapping
    public ResponseEntity<Void> createLink(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId,
            @RequestBody ExperienceLinkCreateRequest req
    ) {
        linkService.create(memberId, experienceId, req);
        return ResponseEntity.noContent().build();
    }

    // 링크 목록 조회
    @GetMapping
    public ResponseEntity<List<ExperienceLinkResponse>> listLinks(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId
    ) {
        return ResponseEntity.ok(linkService.list(memberId, experienceId));
    }

    // 링크 수정
    @PatchMapping("/{linkId}")
    public ResponseEntity<Void> updateLink(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId,
            @PathVariable Long linkId,
            @RequestBody ExperienceLinkUpdateRequest req
    ) {
        linkService.update(memberId, experienceId, linkId, req);
        return ResponseEntity.noContent().build();
    }

    // 링크 삭제
    @DeleteMapping("/{linkId}")
    public ResponseEntity<Void> deleteLink(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId,
            @PathVariable Long linkId
    ) {
        linkService.delete(memberId, experienceId, linkId);
        return ResponseEntity.noContent().build();
    }
}
