package com.moayo.moayobackend.user.ai.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_embedding")
public class UserEmbedding extends BaseEntity {

    @Id
    private Long userId;

    @Column(nullable = false)
    private int dim;

    @Lob
    @Column(nullable = false)
    private String vectorJson;

    @Column(nullable = false)

    public static UserEmbedding of(Long userId, int dim, String vectorJson) {
        UserEmbedding e = new UserEmbedding();
        e.userId = userId;
        e.dim = dim;
        e.vectorJson = vectorJson;
        return e;
    }

    public void update(int dim, String vectorJson) {
        this.dim = dim;
        this.vectorJson = vectorJson;
    }
}
