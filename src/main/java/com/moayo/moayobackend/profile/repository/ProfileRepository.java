package com.moayo.moayobackend.profile.repository;

import com.moayo.moayobackend.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 ProfileRepository
 - profiles 테이블 접근
*/
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);
}
