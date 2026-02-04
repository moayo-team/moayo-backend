package com.moayo.moayobackend.post.service;

import com.moayo.moayobackend.post.entity.Category;
import com.moayo.moayobackend.post.entity.Post;
import com.moayo.moayobackend.post.dto.PostResponseDto;
import com.moayo.moayobackend.post.repository.PostRepository;
import com.moayo.moayobackend.user.entity.User;
import com.moayo.moayobackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 모집글 리스트 조회
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPosts(Long userId, Category category, Pageable pageable) {
        LocalDate today = LocalDate.now();
        Page<Post> posts = (category == null) ?
                postRepository.findAllByDeadlineGreaterThanEqualOrDeadlineIsNullOrderByCreatedAtDesc(today, pageable) :
                postRepository.findAllByCategoryAndDeadlineGreaterThanEqualOrDeadlineIsNullOrderByCreatedAtDesc(category, today, pageable);
        return posts.map(PostResponseDto::new);
    }

    // 모집글 상세 정보 조회
    @Transactional(readOnly = true)
    public PostResponseDto getPostDetail(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + postId));
        return new PostResponseDto(post);
    }

    // 새 모집글 등록
    // 새 모집글 등록
    @Transactional
    public Long createPost(Long userId, com.moayo.moayobackend.post.dto.PostRequestDto request) {
        // 1. 토큰의 userId로 DB에서 실제 유저 정보(닉네임 등) 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + userId));

        // 2. DTO -> Entity 변환
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setRole(request.getRole());
        post.setTotalCount(request.getTotalCount());
        post.setDeadline(request.getDeadline());

        // 3. 작성자 정보 설정
        post.setAuthorId(userId);
        post.setAuthorNickname(user.getName());

        return postRepository.save(post).getPostId();
    }

    // 게시글 내 모든 항목 수정 가능
    @Transactional
    public void updatePost(Long userId, Long postId, String title, String content, Category category, String role, Integer count, LocalDate deadline) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + postId));

        // 본인 확인 로직
        if (!post.getAuthorId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 글만 수정할 수 있습니다.");
        }

        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        post.setRole(role);
        post.setTotalCount(count);
        post.setDeadline(deadline);
    }

    // 내 게시글 삭제
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + postId));

        // 본인 확인 로직
        if (!post.getAuthorId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 글만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    // 내 게시글 조회
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getMyPosts(Long userId, Pageable pageable) {
        return postRepository.findAllByAuthorIdOrderByCreatedAtDesc(userId, pageable)
                .map(PostResponseDto::new);
    }

    // 홈화면 - 마감임박 게시글 조회
    @Transactional
    public List<PostResponseDto> getImminentPostsForHome(int limit) {
        LocalDate today = LocalDate.now();

        return postRepository
                .findImminentPosts(today, PageRequest.of(0, limit))
                .stream()
                .map(PostResponseDto::new)
                .toList();
    }
}