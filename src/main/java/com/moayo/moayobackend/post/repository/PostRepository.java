package com.moayo.moayobackend.post.repository;

import com.moayo.moayobackend.post.entity.Category;
import com.moayo.moayobackend.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 마감일이 안지났거나 마감일이 없는(null) 글만 최신순으로 조회
    Page<Post> findAllByDeadlineGreaterThanEqualOrDeadlineIsNullOrderByCreatedAtDesc(LocalDate now, Pageable pageable);
    Page<Post> findAllByCategoryAndDeadlineGreaterThanEqualOrDeadlineIsNullOrderByCreatedAtDesc(Category category, LocalDate now, Pageable pageable);
    // 작성자 닉네임으로 조회하는 메서드 추가
    Page<Post> findAllByAuthorNicknameOrderByCreatedAtDesc(String nickname, Pageable pageable);
}