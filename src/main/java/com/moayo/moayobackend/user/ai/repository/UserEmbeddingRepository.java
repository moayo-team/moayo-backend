package com.moayo.moayobackend.user.ai.repository;

import com.moayo.moayobackend.user.ai.entity.UserEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEmbeddingRepository extends JpaRepository<UserEmbedding, Long> {
}
