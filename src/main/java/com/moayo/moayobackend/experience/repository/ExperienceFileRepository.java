package com.moayo.moayobackend.experience.repository;

import com.moayo.moayobackend.experience.entity.ExperienceFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceFileRepository extends JpaRepository<ExperienceFile, Long> {

    List<ExperienceFile> findAllByExperienceIdOrderByIdDesc(Long experienceId);

    void deleteByExperienceIdAndFileId(Long experienceId, Long fileId);

    boolean existsByExperienceIdAndFileId(Long experienceId, Long fileId);
}
