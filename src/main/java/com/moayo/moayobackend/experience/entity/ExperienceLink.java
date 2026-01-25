package com.moayo.moayobackend.experience.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "experience_link")
public class ExperienceLink {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long experienceId;

    private String title;

    @Column(length = 1000)
    private String url;

    public ExperienceLink(Long experienceId, String title, String url) {
        this.experienceId = experienceId;
        this.title = title;
        this.url = url;
    }

    public void update(String title, String url) {
        if (title != null)
            this.title = title;
        if (url != null)
            this.url = url;
    }
}