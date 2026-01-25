package com.moayo.moayobackend.user.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_oauth", columnNames = {"oauth_provider", "oauth_sub"})
        },
        indexes = {
                @Index(name = "idx_users_oauth", columnList = "oauth_provider, oauth_sub")
        }
)
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "oauth_provider", nullable = false)
    private String oauthProvider; // google

    @Column(name = "oauth_sub", nullable = false)
    private String oauthSub; // Google sub

    @Column(nullable = false, unique = true)
    private String email; // ERD: 구글 이메일 unique

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber; // 선택

    public static User createGoogleUser(String oauthSub, String email, String name) {
        User u = new User();
        u.oauthProvider = "google";
        u.oauthSub = oauthSub;
        u.email = email;
        u.name = (name == null || name.isBlank()) ? email : name;
        return u;
    }

    public void updateFromGoogle(String email, String name) {
        if (email != null && !email.isBlank()) this.email = email;
        if (name != null && !name.isBlank()) this.name = name;
    }
}
