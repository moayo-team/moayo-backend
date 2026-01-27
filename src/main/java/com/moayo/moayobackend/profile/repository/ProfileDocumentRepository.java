package com.moayo.moayobackend.profile.repository;

import com.moayo.moayobackend.profile.entity.ProfileDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
 ProfileDocumentRepository
 - 프로필 학력첨부(파일) 접근
*/
public interface ProfileDocumentRepository extends JpaRepository<ProfileDocument, Long> {
    List<ProfileDocument> findAllByProfileIdOrderByIdAsc(Long profileId);
    long countByProfileId(Long profileId);
}
