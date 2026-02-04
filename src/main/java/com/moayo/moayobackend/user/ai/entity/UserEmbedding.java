package com.moayo.moayobackend.user.ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_embedding")
public class UserEmbedding {

    @Id
    private Long userId;

    @Column(nullable = false)
    private int dim;

    @Lob
    @Column(nullable = false)
    private String vectorJson;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static UserEmbedding of(Long userId, int dim, String vectorJson) {
        UserEmbedding e = new UserEmbedding();
        e.userId = userId;
        e.dim = dim;
        e.vectorJson = vectorJson;
        e.updatedAt = LocalDateTime.now();
        return e;
    }

    public void update(int dim, String vectorJson) {
        this.dim = dim;
        this.vectorJson = vectorJson;
        this.updatedAt = LocalDateTime.now();
    }
}
