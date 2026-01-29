package com.moayo.moayobackend.experience.controller;

import com.moayo.moayobackend.experience.dto.request.AttachFileRequest;
import com.moayo.moayobackend.experience.dto.response.FileAttachmentResponse;
import com.moayo.moayobackend.experience.service.ExperienceAttachmentService;
import com.moayo.moayobackend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/experiences/{experienceId}/attachments/files")
public class ExperienceAttachmentController {

    private final ExperienceAttachmentService attachmentService;

    // 파일 첨부(연결)
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> attachFile(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @RequestBody AttachFileRequest req
    ) {
        attachmentService.attachFile(userId, experienceId, req);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "파일 첨부 성공", null));
    }

    // 첨부 파일 목록
    @GetMapping
    public ResponseEntity<ApiResponse<List<FileAttachmentResponse>>> listFiles(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId
    ) {
        var result = attachmentService.listFiles(userId, experienceId);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "첨부 파일 목록 조회 성공", result));
    }

    // 첨부 파일 삭제(연결 해제)
    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<Void>> detachFile(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @PathVariable Long fileId
    ) {
        attachmentService.detachFile(userId, experienceId, fileId);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "첨부 파일 삭제 성공", null));
    }
}
