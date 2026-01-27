package com.moayo.moayobackend.profile.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/*
 Profile
 - users 1:1 프로필 엔티티
 - 기능명세: bio/university/major는 필수 (비우면 오류)
*/
@Getter
@NoArgsConstructor
@Entity
@Table(name = "profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_profiles_user_id", columnNames = {"user_id"}))
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name="image_url")
    private String imageUrl;

    @Column(nullable = false, length = 500)
    private String bio;

    @Column(nullable = false, length = 100)
    private String university;

    @Column(nullable = false, length = 100)
    private String major;

    public Profile(Long userId, String imageUrl, String bio, String university, String major) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.university = university;
        this.major = major;
    }

    public void update(String imageUrl, String bio, String university, String major) {
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (bio != null) this.bio = bio;
        if (university != null) this.university = university;
        if (major != null) this.major = major;
    }
}
