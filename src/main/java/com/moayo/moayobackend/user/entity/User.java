package com.moayo.moayobackend.user.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import com.moayo.moayobackend.profile.entity.InterestTag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/*
 User
 - BaseEntity 상속: 생성/수정 시간 자동 관리
 - 로그인(OAuth) 기반 사용자 엔티티
*/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_oauth", columnNames = {"oauth_provider", "oauth_sub"}),
                @UniqueConstraint(name = "uk_users_email", columnNames = {"email"})
        })
public class User extends BaseEntity { // BaseEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="oauth_provider", nullable = false, length = 20)
    private String oauthProvider;

    @Column(name="oauth_sub", nullable = false, length = 100)
    private String oauthSub;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 6)
    private String name;

    @Column(name="phone_number", length = 11)
    private String phoneNumber;

    @Column(nullable = false, length = 20)
    private String role;

    // 서비스에서 사용하는 구글 사용자 생성 메서드
    public static User createGoogleUser(String sub, String email, String name) {
        User user = new User();
        user.oauthProvider = "google";
        user.oauthSub = sub;
        user.email = email;
        user.role = "USER";
        user.name = (name != null && name.length() > 6) ? name.substring(0, 6) : name;
        return user;
    }

    // 서비스에서 사용하는 구글 정보 업데이트 메서드
    public void updateFromGoogle(String email, String name) {
        this.email = email;
        this.name = (name != null && name.length() > 6) ? name.substring(0, 6) : name;
    }

    // 기존 프로필 수정 메서드
    public void updateBasics(String name, String phoneNumber) {
        if (name != null) this.name = name;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
    }
}