package com.moayo.moayobackend.experience.repository;

import com.moayo.moayobackend.experience.entity.ExperienceLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExperienceLinkRepository extends JpaRepository<ExperienceLink, Long> {

    List<ExperienceLink> findAllByExperienceIdOrderByIdDesc(Long experienceId);

    Optional<ExperienceLink> findByIdAndExperienceId(Long id, Long experienceId);
}
