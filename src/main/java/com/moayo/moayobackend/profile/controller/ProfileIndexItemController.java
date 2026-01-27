package com.moayo.moayobackend.profile.controller;

import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.profile.dto.request.ProfileIndexItemCreateRequest;
import com.moayo.moayobackend.profile.dto.request.ProfileIndexItemUpdateRequest;
import com.moayo.moayobackend.profile.service.ProfileIndexItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/*
 ProfileIndexItemController
 - 프로필 기본정보 추가 항목 CRUD
 - 기능명세: 최대 4개 제한은 서비스에서 검증
*/

@Tag(name = "프로필 기본정보 추가", description = "프로필 기본정보 조회/생성/수정/삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles/me/index-items")
public class ProfileIndexItemController {

    private final ProfileIndexItemService profileIndexItemService;

    @Operation(summary = "기본정보 추가 항목 조회", description = "내가 등록한 기본정보 추가 항목을 조회합니다.")
    @GetMapping
    public ApiResponse<?> list(@AuthenticationPrincipal Long userId) {
        Long targetId = (userId != null) ? userId : 1L;
        return ApiResponse.ok("SUCCESS", "기본정보 추가 항목 조회에 성공했습니다.", profileIndexItemService.findMine(userId));
    }

    @Operation(summary = "기본정보 추가 생성", description = "기본정보 추가 항목을 생성합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> create(
            @AuthenticationPrincipal Long userId,
            @RequestPart("data") @Valid ProfileIndexItemCreateRequest req,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        Long targetId = (userId != null) ? userId : 1L;
        profileIndexItemService.create(targetId, req, file);
        return ApiResponse.ok("SUCCESS", "기본정보 추가 항목이 생성되었습니다.", null);
    }

    @Operation(summary = "기본정보 추가 항목 수정", description = "내가 등록한 기본정보 추가 항목을 수정합니다.")
    @PatchMapping(value = "/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long itemId,
            @RequestPart("data") @Valid ProfileIndexItemUpdateRequest req,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        Long targetId = (userId != null) ? userId : 1L;
        profileIndexItemService.update(targetId, itemId, req, file);
        return ApiResponse.ok("SUCCESS", "기본정보 추가 항목이 수정되었습니다.", null);
    }

    @Operation(summary = "기본정보 추가 항목 삭제", description = "내가 등록한 기본정보 추가 항목을 삭제합니다.")
    @DeleteMapping("/{itemId}")
    public ApiResponse<?> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long itemId
    ) {
        Long targetId = (userId != null) ? userId : 1L;
        profileIndexItemService.delete(targetId, itemId);
        return ApiResponse.ok("SUCCESS", "기본정보 추가 항목이 삭제되었습니다.", null);
    }
}
