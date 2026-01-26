package com.moayo.moayobackend.profile.repository;

import com.moayo.moayobackend.profile.entity.UserInterestTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestTagRepository extends JpaRepository<UserInterestTag, Long> {
    List<UserInterestTag> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
