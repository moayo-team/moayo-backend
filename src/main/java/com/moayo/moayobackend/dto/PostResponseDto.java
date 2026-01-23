package com.moayo.moayobackend.dto;

import com.moayo.moayobackend.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String title;
    private String summary;
    private String categoryLabel;
    private String dDay;
    private String authorNickname;
    private String profileImageUrl;
    private String role;
    private String content;
    private String totalCount;

    public PostResponseDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.authorNickname = post.getAuthorNickname();
        this.profileImageUrl = post.getProfileImageUrl();
        this.role = post.getRole();
        this.content = post.getContent();

        // 1. 카테고리 한글명 변환
        this.categoryLabel = post.getCategory() != null ? post.getCategory().getLabel() : "기타";

        // 2. 모집 인원 처리 (숫자 or 미정)
        if (post.getTotalCount() == null || post.getTotalCount() == 0) {
            this.totalCount = "미정";
        } else {
            this.totalCount = post.getTotalCount() + "명";
        }

        // 3. 본문 요약
        if (post.getContent() != null && post.getContent().length() > 100) {
            this.summary = post.getContent().substring(0, 100) + "...";
        } else {
            this.summary = post.getContent();
        }

        // 4. D-Day 계산 로직
        if (post.getDeadline() == null) {
            this.dDay = "상시모집";
        } else {
            long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), post.getDeadline());
            if (daysBetween < 0) {
                this.dDay = "마감";
            } else if (daysBetween == 0) {
                this.dDay = "D-Day";
            } else {
                this.dDay = "D-" + daysBetween;
            }
        }
    }
}