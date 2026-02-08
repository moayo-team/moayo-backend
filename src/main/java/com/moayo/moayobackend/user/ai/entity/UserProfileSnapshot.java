package com.moayo.moayobackend.user.ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_profile_snapshot")
public class UserProfileSnapshot {

    @Id
    private Long userId;

    @Lob
    @Column(nullable = false)
    private String text;

    @Column(nullable = false)

    public static UserProfileSnapshot of(Long userId, String text) {
        UserProfileSnapshot s = new UserProfileSnapshot();
        s.userId = userId;
        s.text = text;
        return s;
    }

    public void updateText(String text) {
        this.text = text;
    }
}
