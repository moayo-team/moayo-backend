package com.moayo.moayobackend.experience.service;

import com.moayo.moayobackend.experience.dto.request.AttachFileRequest;
import com.moayo.moayobackend.experience.dto.response.FileAttachmentResponse;
import com.moayo.moayobackend.experience.entity.Experience;
import com.moayo.moayobackend.experience.entity.ExperienceFile;
import com.moayo.moayobackend.experience.repository.ExperienceFileRepository;
import com.moayo.moayobackend.experience.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceAttachmentService {

    private final ExperienceRepository experienceRepository;
    private final ExperienceFileRepository fileRepository;

    @Transactional
    public void attachFile(Long userId, Long experienceId, AttachFileRequest req) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        if (req == null || req.fileId() == null) {
            throw new IllegalArgumentException("fileId is required");
        }

        boolean exists = fileRepository.existsByExperience_IdAndFileId(experienceId, req.fileId());
        if (exists) return;

        fileRepository.save(new ExperienceFile(e, req.fileId(), req.fileName()));
    }

    @Transactional(readOnly = true)
    public List<FileAttachmentResponse> listFiles(Long userId, Long experienceId) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        return fileRepository.findAllByExperience_IdOrderByCreatedAtDesc(experienceId).stream()
                .map(f -> new FileAttachmentResponse(f.getFileId(), f.getFileName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FileAttachmentResponse> listPublicFiles(Long experienceId) {
        Experience e = experienceRepository.findByIdAndVisibleTrue(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found or not public"));

        return fileRepository.findAllByExperience_IdOrderByCreatedAtDesc(experienceId).stream()
                .map(f -> new FileAttachmentResponse(f.getFileId(), f.getFileName()))
                .toList();
    }

    @Transactional
    public void detachFile(Long userId, Long experienceId, Long fileId) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        fileRepository.deleteByExperience_IdAndFileId(experienceId, fileId);
    }
}
