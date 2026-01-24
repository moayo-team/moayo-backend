package com.moayo.moayobackend.post.controller;

import com.moayo.moayobackend.post.entity.Category;
import com.moayo.moayobackend.post.entity.Post;
import com.moayo.moayobackend.post.dto.PostResponseDto;
import com.moayo.moayobackend.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 모집글 리스트 조회
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @RequestParam(required = false) Category category,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(category, pageable));
    }

    // 모집글 상세 정보 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostDetail(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostDetail(postId));
    }

    // 새 모집글 등록
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody Post post) {
        return ResponseEntity.ok(postService.createPost(post));
    }

    // 내 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<Void> update(
            @PathVariable Long postId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Integer count,
            @RequestParam Category category,
            @RequestParam String role,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline) {

        postService.updatePost(postId, title, content, category, role, count, deadline);
        return ResponseEntity.ok().build();
    }

    // 내 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // 내 게시글 모아보기
    @GetMapping("/me")
    public ResponseEntity<Page<PostResponseDto>> getMyPosts(
            @RequestParam String nickname,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(postService.getMyPosts(nickname, pageable));
    }
}