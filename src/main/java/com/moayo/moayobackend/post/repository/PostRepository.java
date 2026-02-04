package com.moayo.moayobackend.post.repository;

import com.moayo.moayobackend.post.entity.Category;
import com.moayo.moayobackend.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 마감일이 안지났거나 마감일이 없는(null) 글만 최신순으로 조회
    Page<Post> findAllByDeadlineGreaterThanEqualOrDeadlineIsNullOrderByCreatedAtDesc(LocalDate now, Pageable pageable);
    Page<Post> findAllByCategoryAndDeadlineGreaterThanEqualOrDeadlineIsNullOrderByCreatedAtDesc(Category category, LocalDate now, Pageable pageable);

    Page<Post> findAllByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    // 홈 - 마감 임박 게시글 조회
    // 마감일이 없는(null) 제외, 마감일이 가까운 순서대로 정렬,
    // limit으로 글 수 제어 : PageRequest.of(0, n)
    @Query("""
        select p
        from Post p
        where p.deadline is not null
            and p.deadline >= :today
        order by p.deadline asc
    """)
    List<Post> findImminentPosts(
            @Param("today") LocalDate today,
            Pageable pageable
    );

}