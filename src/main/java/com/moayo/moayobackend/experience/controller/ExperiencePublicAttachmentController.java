package com.moayo.moayobackend.experience.controller;

import com.moayo.moayobackend.experience.dto.response.ExperienceLinkResponse;
import com.moayo.moayobackend.experience.dto.response.FileAttachmentResponse;
import com.moayo.moayobackend.experience.service.ExperienceAttachmentService;
import com.moayo.moayobackend.experience.service.ExperienceLinkService;
import com.moayo.moayobackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "이력서 공개 첨부 조회", description = "공개 이력서의 파일/링크 첨부를 조회하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/experiences/public/{experienceId}/attachments")
public class ExperiencePublicAttachmentController {

    private final ExperienceAttachmentService attachmentService;
    private final ExperienceLinkService linkService;

    @Operation(summary = "공개 이력서 링크 첨부 목록 조회", description = "공개 이력서(visible=true)에 첨부된 링크 목록을 조회합니다.")
    @GetMapping("/links")
    public ResponseEntity<ApiResponse<List<ExperienceLinkResponse>>> listPublicLinks(
            @PathVariable Long experienceId
    ) {
        var result = linkService.listPublicLinks(experienceId);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "공개 첨부 링크 목록 조회 성공", result));
    }

    @Operation(summary = "공개 이력서 파일 첨부 목록 조회", description = "공개 이력서(visible=true)에 첨부된 파일 목록을 조회합니다.")
    @GetMapping("/files")
    public ResponseEntity<ApiResponse<List<FileAttachmentResponse>>> listPublicFiles(
            @PathVariable Long experienceId
    ) {
        var result = attachmentService.listPublicFiles(experienceId);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "공개 첨부 파일 목록 조회 성공", result));
    }
}
