package com.moayo.moayobackend.profile.repository;

import com.moayo.moayobackend.profile.entity.InterestTag;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 InterestTagRepository
 - interest_tags 마스터 테이블 접근
*/
public interface InterestTagRepository extends JpaRepository<InterestTag, Long> {
    boolean existsById(Long id);
    boolean existsByName(String name);
}
