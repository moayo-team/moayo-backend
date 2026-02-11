package com.moayo.moayobackend.experience.controller;

import com.moayo.moayobackend.experience.dto.request.AttachFileRequest;
import com.moayo.moayobackend.experience.dto.response.FileAttachmentResponse;
import com.moayo.moayobackend.experience.service.ExperienceAttachmentService;
import com.moayo.moayobackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "이력서 파일 첨부", description = "이력서에 파일을 첨부/조회/삭제하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experiences/{experienceId}/attachments/files")
public class ExperienceAttachmentController {

    private final ExperienceAttachmentService attachmentService;

    @Operation(summary = "이력서 첨부 파일 추가", description = "업로드된 파일을 이력서 항목에 연결합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> attachFile(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @RequestBody AttachFileRequest req
    ) {
        attachmentService.attachFile(userId, experienceId, req);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "파일 첨부 성공", null)
        );
    }

    @Operation(summary = "이력서 파일 첨부 목록 조회", description = "이력서에 첨부된 파일 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<FileAttachmentResponse>>> listFiles(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId
    ) {
        var result = attachmentService.listFiles(userId, experienceId);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "첨부 파일 목록 조회 성공", result)
        );
    }

    @Operation(summary = "이력서 첨부 파일 삭제", description = "이력서에 첨부된 파일 연결을 해제합니다.")
    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<Void>> detachFile(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long experienceId,
            @PathVariable Long fileId
    ) {
        attachmentService.detachFile(userId, experienceId, fileId);
        return ResponseEntity.ok(
                ApiResponse.ok("SUCCESS-200", "첨부 파일 삭제 성공", null)
        );
    }
}
