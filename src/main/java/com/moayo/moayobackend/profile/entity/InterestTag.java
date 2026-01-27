package com.moayo.moayobackend.profile.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 InterestTag
 - 관심태그 마스터 테이블
 - 사전 정의 목록만 존재
*/
@Getter
@NoArgsConstructor
@Entity
@Table(name = "interest_tags",
        uniqueConstraints = @UniqueConstraint(name = "uk_interest_tags_name", columnNames = {"name"}))
public class InterestTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    public InterestTag(String name) {
        this.name = name;
    }
}
