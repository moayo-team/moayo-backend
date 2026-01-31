package com.moayo.moayobackend.post.controller;

import com.moayo.moayobackend.post.entity.Category;
import com.moayo.moayobackend.post.entity.Post;
import com.moayo.moayobackend.post.dto.PostResponseDto;
import com.moayo.moayobackend.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.moayo.moayobackend.post.dto.PostRequestDto;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Post API", description = "게시판 모집글 관련 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 1. 모집글 리스트 조회
    @Operation(summary = "전체 모집글 조회", description = "모든 모집글을 최신순으로 조회하며, 카테고리 필터링이 가능")
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @io.swagger.v3.oas.annotations.Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Category category,
            @org.springdoc.core.annotations.ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(userId, category, pageable));
    }

    // 2. 모집글 상세 정보 조회
    @Operation(summary = "모집글 상세 조회", description = "특정 ID의 게시글 상세 정보를 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostDetail(
            @io.swagger.v3.oas.annotations.Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostDetail(userId, postId));
    }

    // 3. 새 모집글 등록
    @Operation(summary = "새 모집글 등록", description = "로그인한 사용자가 새로운 모집글을 작성합니다.")
    @PostMapping
    public ResponseEntity<Long> create(
            @io.swagger.v3.oas.annotations.Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostRequestDto requestDto) {
        return ResponseEntity.ok(postService.createPost(userId, requestDto));
    }

    // 4. 내 게시글 수정
    @Operation(summary = "내 게시글 수정", description = "내가 작성한 게시글의 내용을 수정합니다.")
    @PatchMapping("/{postId}")
    public ResponseEntity<Void> update(
            @io.swagger.v3.oas.annotations.Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @RequestBody PostRequestDto requestDto) {
        
        postService.updatePost(userId, postId, 
                requestDto.getTitle(), 
                requestDto.getContent(), 
                requestDto.getCategory(), 
                requestDto.getRole(), 
                requestDto.getTotalCount(), 
                requestDto.getDeadline());
        return ResponseEntity.ok().build();
    }

    // 5. 내 게시글 삭제
    @Operation(summary = "내 게시글 삭제", description = "내가 작성한 게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            @io.swagger.v3.oas.annotations.Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @PathVariable Long postId) {
        postService.deletePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    // 6. 내 게시글 모아보기
    @Operation(summary = "내 게시글 모아보기", description = "로그인한 본인이 작성한 게시글 목록만 모아서 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<Page<PostResponseDto>> getMyPosts(
            @io.swagger.v3.oas.annotations.Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @org.springdoc.core.annotations.ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getMyPosts(userId, pageable));
    }
}