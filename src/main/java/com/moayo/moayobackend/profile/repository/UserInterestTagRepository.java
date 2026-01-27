package com.moayo.moayobackend.profile.repository;

import com.moayo.moayobackend.profile.entity.UserInterestTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/*
 UserInterestTagRepository
 - 유저의 관심태그 매핑 접근
*/
public interface UserInterestTagRepository extends JpaRepository<UserInterestTag, Long> {
    List<UserInterestTag> findAllByUserId(Long userId);

    @Modifying // 이게 없으면 500 에러가 발생합니다!
    @Query("delete from UserInterestTag u where u.userId = :userId")
    void deleteAllByUserId(Long userId);
}
