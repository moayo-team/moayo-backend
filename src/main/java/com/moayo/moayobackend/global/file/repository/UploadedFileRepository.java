package com.moayo.moayobackend.global.file.repository;

import com.moayo.moayobackend.global.file.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
}
