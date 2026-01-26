package com.moayo.moayobackend.experience.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "experience_file")
public class ExperienceFile extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experience_id", nullable = false)
    private Experience experience;

    @Column(nullable = false)
    private Long fileId;

    private String fileName;

    public ExperienceFile(Experience experience, Long fileId, String fileName) {
        this.experience = experience;
        this.fileId = fileId;
        this.fileName = fileName;
    }
}