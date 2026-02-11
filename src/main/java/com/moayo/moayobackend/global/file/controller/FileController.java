package com.moayo.moayobackend.global.file.controller;

import com.moayo.moayobackend.global.file.dto.UploadFileResponse;
import com.moayo.moayobackend.global.file.service.FileStorageService;
import com.moayo.moayobackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Tag(name = "파일(File)", description = "파일 업로드/다운로드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "파일 업로드", description = "multipart/form-data로 파일을 업로드하고 fileId를 발급합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadFileResponse>> upload(
            @AuthenticationPrincipal Long userId,
            @RequestPart("file") MultipartFile file
    ) {
        UploadFileResponse result = fileStorageService.upload(userId, file);
        return ResponseEntity.ok(ApiResponse.ok("SUCCESS-200", "파일 업로드 성공", result));
    }

    @Operation(summary = "파일 다운로드", description = "fileId로 파일을 다운로드합니다.")
    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> download(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long fileId
    ) {
        // (권한 체크 필요하면 여기서 userId 기반으로 검사 로직 추가)
        var meta = fileStorageService.getMeta(fileId);
        Resource resource = fileStorageService.loadAsResource(fileId);

        String encodedName = URLEncoder.encode(meta.getOriginalFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        String contentType = (meta.getContentType() == null || meta.getContentType().isBlank())
                ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                : meta.getContentType();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .body(resource);
    }
}
