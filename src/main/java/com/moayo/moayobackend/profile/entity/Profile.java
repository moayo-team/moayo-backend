package com.moayo.moayobackend.profile.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "text")
    private String bio;

    @Column(nullable = false, length = 100)
    private String university;

    @Column(nullable = false, length = 100)
    private String major;

    public static Profile create(Long userId, String bio, String university, String major, String imageUrl) {
        Profile p = new Profile();
        p.userId = userId;
        p.bio = bio;
        p.university = university;
        p.major = major;
        p.imageUrl = imageUrl;
        return p;
    }

    public void update(String bio, String university, String major, String imageUrl) {
        if (bio != null) this.bio = bio;
        if (university != null) this.university = university;
        if (major != null) this.major = major;
        if (imageUrl != null) this.imageUrl = imageUrl;
    }
}
