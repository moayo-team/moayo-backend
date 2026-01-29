package com.moayo.moayobackend.profile.repository;

import com.moayo.moayobackend.profile.entity.ProfileIndexItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 ProfileIndexItemRepository
 - 프로필 기본정보 추가 항목 접근
*/

@Repository
public interface ProfileIndexItemRepository extends JpaRepository<ProfileIndexItem, Long> {
    List<ProfileIndexItem> findAllByProfileIdOrderByIdAsc(Long profileId);
    long countByProfileId(Long profileId);
}
