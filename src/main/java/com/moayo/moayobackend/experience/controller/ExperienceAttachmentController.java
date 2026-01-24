package com.moayo.moayobackend.experience.controller;

import com.moayo.moayobackend.experience.service.ExperienceAttachmentService;
import com.moayo.moayobackend.experience.dto.request.AttachFileRequest;
import com.moayo.moayobackend.experience.dto.response.FileAttachmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experiences/{experienceId}/attachments/files")
public class ExperienceAttachmentController {

    private final ExperienceAttachmentService attachmentService;

    // 파일 첨부(연결) - fileId를 받아 experience에 연결
    @PostMapping
    public ResponseEntity<Void> attachFile(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId,
            @RequestBody AttachFileRequest req
    ) {
        attachmentService.attachFile(memberId, experienceId, req);
        return ResponseEntity.noContent().build();
    }

    // 첨부된 파일 목록 조회
    @GetMapping
    public ResponseEntity<List<FileAttachmentResponse>> listFiles(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId
    ) {
        return ResponseEntity.ok(attachmentService.listFiles(memberId, experienceId));
    }

    // 첨부 파일 삭제(연결 해제)
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> detachFile(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long experienceId,
            @PathVariable Long fileId
    ) {
        attachmentService.detachFile(memberId, experienceId, fileId);
        return ResponseEntity.noContent().build();
    }
}
