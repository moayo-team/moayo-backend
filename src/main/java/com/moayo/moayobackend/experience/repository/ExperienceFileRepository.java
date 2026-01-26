package com.moayo.moayobackend.experience.repository;

import com.moayo.moayobackend.experience.entity.ExperienceFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceFileRepository extends JpaRepository<ExperienceFile, Long> {

    List<ExperienceFile> findAllByExperience_IdOrderByCreatedAtDesc(Long experienceId);

    boolean existsByExperience_IdAndFileId(Long experienceId, Long fileId);

    void deleteByExperience_IdAndFileId(Long experienceId, Long fileId);
}
