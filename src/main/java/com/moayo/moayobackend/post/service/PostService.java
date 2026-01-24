package com.moayo.moayobackend.post.service;

import com.moayo.moayobackend.post.entity.Category;
import com.moayo.moayobackend.post.entity.Post;
import com.moayo.moayobackend.post.dto.PostResponseDto;
import com.moayo.moayobackend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    // 모집글 리스트 조회 (카테고리별 필터링 가능)
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPosts(Category category, Pageable pageable) {
        LocalDate today = LocalDate.now();
        Page<Post> posts = (category == null) ?
                postRepository.findAllByDeadlineGreaterThanEqualOrDeadlineIsNullOrderByCreatedAtDesc(today, pageable) :
                postRepository.findAllByCategoryAndDeadlineGreaterThanEqualOrDeadlineIsNullOrderByCreatedAtDesc(category, today, pageable);
        return posts.map(PostResponseDto::new);
    }
     // 모집글 상세 정보 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + postId));
        return new PostResponseDto(post);
    }
    // 새 모집글 등록
    @Transactional
    public Long createPost(Post request) {
        return postRepository.save(request).getPostId();
    }

    // 게시글 내 모든 항목 수정 가능
    @Transactional
    public void updatePost(Long postId, String title, String content, Category category, String role, Integer count, LocalDate deadline) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + postId));

        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        post.setRole(role);
        post.setTotalCount(count);
        post.setDeadline(deadline);
    }
    // 내 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
    // 내 게시글 조회
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getMyPosts(String nickname, Pageable pageable) {
        return postRepository.findAllByAuthorNicknameOrderByCreatedAtDesc(nickname, pageable)
                .map(PostResponseDto::new);
    }
}