package com.moayo.moayobackend.user.repository;

import com.moayo.moayobackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 UserRepository
 - users 테이블 JPA 접근
*/
public interface UserRepository extends JpaRepository<User, Long> {
    java.util.Optional<User> findByOauthProviderAndOauthSub(String oauthProvider, String oauthSub);
}
