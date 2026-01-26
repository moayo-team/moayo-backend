package com.moayo.moayobackend.profile.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_interest_tags",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_interest_tags", columnNames = {"user_id", "interest_tag_id"})
)
public class UserInterestTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="interest_tag_id", nullable = false)
    private Long interestTagId;

    public static UserInterestTag create(Long userId, Long tagId) {
        UserInterestTag u = new UserInterestTag();
        u.userId = userId;
        u.interestTagId = tagId;
        return u;
    }
}
