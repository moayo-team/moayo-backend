package com.moayo.moayobackend.experience.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "experience_file")
public class ExperienceFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long experienceId;

    private Long fileId;

    private String fileName;

    public ExperienceFile(Long experienceId, Long fileId, String fileName) {
        this.experienceId = experienceId;
        this.fileId = fileId;
        this.fileName = fileName;
    }
}