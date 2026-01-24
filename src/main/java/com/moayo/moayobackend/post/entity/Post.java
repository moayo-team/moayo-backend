package com.moayo.moayobackend.post.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false, length = 50)
    @Size(max = 50, message = "제목은 50자 이내여야 합니다.")
    private String title;

    @Column(nullable = false, length = 500)
    @Size(max = 500, message = "본문은 500자 이내여야 합니다.")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private String role;
    private Integer totalCount;
    private LocalDate deadline;

    private String authorNickname;
    private String profileImageUrl = "default_url";

    private LocalDateTime createdAt = LocalDateTime.now();
}