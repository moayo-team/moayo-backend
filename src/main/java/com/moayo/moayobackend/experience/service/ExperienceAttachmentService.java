package com.moayo.moayobackend.experience.service;

import com.moayo.moayobackend.experience.dto.request.AttachFileRequest;
import com.moayo.moayobackend.experience.entity.Experience;
import com.moayo.moayobackend.experience.entity.ExperienceFile;
import com.moayo.moayobackend.experience.dto.response.FileAttachmentResponse;
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
    public void attachFile(Long memberId, Long experienceId, AttachFileRequest req) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(memberId);

        if (req.fileId() == null) throw new IllegalArgumentException("fileId is required");

        boolean exists = fileRepository.existsByExperienceIdAndFileId(experienceId, req.fileId());
        if (exists) return; // 중복 첨부 방지(원하면 예외로 바꿔도 됨)

        fileRepository.save(new ExperienceFile(experienceId, req.fileId(), req.fileName()));
    }

    @Transactional(readOnly = true)
    public List<FileAttachmentResponse> listFiles(Long memberId, Long experienceId) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(memberId);

        return fileRepository.findAllByExperienceIdOrderByIdDesc(experienceId).stream()
                .map(f -> new FileAttachmentResponse(f.getFileId(), f.getFileName()))
                .toList();
    }

    @Transactional
    public void detachFile(Long memberId, Long experienceId, Long fileId) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(memberId);

        fileRepository.deleteByExperienceIdAndFileId(experienceId, fileId);
    }
}
