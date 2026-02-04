package com.moayo.moayobackend.user.repository;

import com.moayo.moayobackend.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

/*
 UserRepository
 - users 테이블 JPA 접근
*/
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOauthProviderAndOauthSub(String oauthProvider, String oauthSub);

    // 추천 후보 유저 조회 (본인 제회, 최신 생성순으로 상위 n명만 가져와서 계산)
    List<User> findByIdNotOrderByCreatedAtDesc(Long id, Pageable pageable);
}
