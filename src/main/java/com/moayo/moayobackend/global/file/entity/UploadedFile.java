package com.moayo.moayobackend.global.file.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "uploaded_file")
public class UploadedFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 업로더(권한 체크 필요하면 활용)
    @Column(nullable = false)
    private Long uploaderId;

    @Column(nullable = false)
    private String originalFileName;

    private String contentType;

    @Column(nullable = false)
    private Long size;

    // 디스크에 저장된 실제 경로
    @Column(nullable = false, length = 500)
    private String storagePath;

    public UploadedFile(Long uploaderId, String originalFileName, String contentType, Long size, String storagePath) {
        this.uploaderId = uploaderId;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.size = size;
        this.storagePath = storagePath;
    }
}
