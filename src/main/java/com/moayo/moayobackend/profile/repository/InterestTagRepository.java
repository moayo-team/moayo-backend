package com.moayo.moayobackend.profile.repository;

import com.moayo.moayobackend.profile.entity.InterestTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestTagRepository extends JpaRepository<InterestTag, Long> {
    Optional<InterestTag> findByName(String name);
    boolean existsById(Long id);
}
