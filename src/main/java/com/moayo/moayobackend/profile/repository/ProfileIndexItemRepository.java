package com.moayo.moayobackend.profile.repository;

import com.moayo.moayobackend.profile.entity.ProfileIndexItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileIndexItemRepository extends JpaRepository<ProfileIndexItem, Long> {
    List<ProfileIndexItem> findByProfileId(Long profileId);
    Optional<ProfileIndexItem> findByIdAndProfileId(Long id, Long profileId);
}
