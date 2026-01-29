package com.moayo.moayobackend.profile.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/*
 UserInterestTag
 - 유저가 선택한 관심태그 매핑 테이블
 - 태그 개수 제한 없음
*/
@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_interest_tags",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_interest_tags", columnNames = {"user_id", "interest_tag_id"}))
public class UserInterestTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="interest_tag_id", nullable = false)
    private Long interestTagId;

    public UserInterestTag(Long userId, Long interestTagId) {
        this.userId = userId;
        this.interestTagId = interestTagId;
    }
}
