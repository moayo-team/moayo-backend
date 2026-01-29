package com.moayo.moayobackend.experience.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "experience_link")
public class ExperienceLink extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experience_id", nullable = false)
    private Experience experience;

    private String title;

    @Column(length = 1000)
    private String url;

    public ExperienceLink(Experience experience, String title, String url) {
        this.experience = experience;
        this.title = title;
        this.url = url;
    }

    public void update(String title, String url) {
        if (title != null) this.title = title;
        if (url != null) this.url = url;
    }
}