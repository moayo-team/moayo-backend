package com.moayo.moayobackend.user.ai.repository;

import com.moayo.moayobackend.user.ai.entity.UserProfileSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileSnapshotRepository extends JpaRepository<UserProfileSnapshot, Long> {
}
