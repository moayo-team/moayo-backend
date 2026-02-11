package com.moayo.moayobackend.global.file.service;

import com.moayo.moayobackend.global.file.dto.UploadFileResponse;
import com.moayo.moayobackend.global.file.entity.UploadedFile;
import com.moayo.moayobackend.global.file.repository.UploadedFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final UploadedFileRepository uploadedFileRepository;

    @Value("${file.storage.path:./uploads}")
    private String storageRoot;

    @Transactional
    public UploadFileResponse upload(Long uploaderId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }

        String originalName = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank())
                ? "unnamed"
                : file.getOriginalFilename();

        String contentType = file.getContentType();
        long size = file.getSize();

        try {
            Path root = Paths.get(storageRoot).toAbsolutePath().normalize();
            Files.createDirectories(root);

            // 실제 저장 파일명: UUID + 원본명
            String safeOriginal = originalName.replaceAll("[\\\\/:*?\"<>|]", "_");
            String savedName = UUID.randomUUID() + "_" + safeOriginal;
            Path savedPath = root.resolve(savedName).normalize();

            // 파일 저장
            Files.copy(file.getInputStream(), savedPath, StandardCopyOption.REPLACE_EXISTING);

            // DB 메타 저장
            UploadedFile saved = uploadedFileRepository.save(
                    new UploadedFile(
                            uploaderId,
                            originalName,
                            contentType,
                            size,
                            savedPath.toString()
                    )
            );

            return new UploadFileResponse(saved.getId(), saved.getOriginalFileName(), saved.getContentType(), saved.getSize());

        } catch (IOException e) {
            throw new IllegalStateException("file upload failed: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public UploadedFile getMeta(Long fileId) {
        return uploadedFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
    }

    @Transactional(readOnly = true)
    public Resource loadAsResource(Long fileId) {
        UploadedFile meta = getMeta(fileId);

        Path path = Paths.get(meta.getStoragePath());
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found on disk");
        }

        return new FileSystemResource(path);
    }
}
