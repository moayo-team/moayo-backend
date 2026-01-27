package com.moayo.moayobackend.profile.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.profile.dto.response.ProfileDocumentResponse;
import com.moayo.moayobackend.profile.service.ProfileDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/*
 ProfileDocumentController
 - 학력첨부(파일) 목록/업로드/삭제 API
 - 업로드는 multipart/form-data로 처리
*/

@Tag(name = "첨부파일 관리", description = "첨부파일 목록 조회/업로드/삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles/me/documents")
public class ProfileDocumentController {

    private final ProfileDocumentService profileDocumentService;

    @Operation(summary = "첨부파일 목록 조회", description = "내가 등록한 첨부파일 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<?> list(@AuthenticationPrincipal Long userId) {
        Long targetId = (userId != null) ? userId : 1L;
        return ApiResponse.ok("SUCCESS", "첨부 파일 목록 조회에 성공했습니다.", profileDocumentService.list(targetId));
    }

    @Operation(summary = "첨부파일 업로드", description = "첨부파일을 업로드합니다.")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<?> upload(
            @AuthenticationPrincipal Long userId,
            @RequestPart("file") MultipartFile file
    ) {
        Long targetId = (userId != null) ? userId : 1L;
        ProfileDocumentResponse res = profileDocumentService.upload(targetId, file);
        return ApiResponse.ok("SUCCESS", "첨부 파일 업로드에 성공했습니다.", res);
    }

    @Operation(summary = "첨부파일 삭제", description = "내가 등록한 첨부파일을 삭제합니다.")
    @DeleteMapping("/{documentId}")
    public ApiResponse<?> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long documentId
    ) {
        Long targetId = (userId != null) ? userId : 1L;
        profileDocumentService.delete(targetId, documentId);
        return ApiResponse.ok("SUCCESS", "학력 첨부 삭제에 성공했습니다.", null);
    }
}
