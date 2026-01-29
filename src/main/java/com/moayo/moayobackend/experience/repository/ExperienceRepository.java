package com.moayo.moayobackend.experience.repository;

import com.moayo.moayobackend.experience.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    List<Experience> findAllByUserIdAndVisibleTrueOrderByCreatedAtDesc(Long userId);
}