package com.moayo.moayobackend.profile.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/*
 ProfileDocument
 - 프로필의 학력첨부(파일) 엔티티
 - 기능명세: pdf/image, 10MB 제한, 최대 20개(서비스 로직)
 - 실제 파일 저장은 서비스에서 처리하고 fileUrl에 접근 경로 저장
*/
@Getter
@NoArgsConstructor
@Entity
@Table(name = "profile_documents",
        indexes = @Index(name = "idx_profile_documents_profile_id", columnList = "profile_id"))
public class ProfileDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="profile_id", nullable = false)
    private Long profileId;

    @Column(name="file_url", nullable = false)
    private String fileUrl;

    @Column(name="file_name")
    private String fileName;

    @Column(name="file_type", length = 50)
    private String fileType;

    @Column(name="file_size")
    private Long fileSize;

    public ProfileDocument(Long profileId, String fileUrl, String fileName, String fileType, Long fileSize) {
        this.profileId = profileId;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
}
