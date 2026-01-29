package com.moayo.moayobackend.profile.service;

import com.moayo.moayobackend.global.exception.BusinessException;
import com.moayo.moayobackend.global.exception.GeneralErrorCode;
import com.moayo.moayobackend.profile.dto.response.ProfileDocumentResponse;
import com.moayo.moayobackend.profile.entity.Profile;
import com.moayo.moayobackend.profile.entity.ProfileDocument;
import com.moayo.moayobackend.profile.exception.ProfileErrorCode;
import com.moayo.moayobackend.profile.repository.ProfileDocumentRepository;
import com.moayo.moayobackend.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/*
 ProfileDocumentService
 - 학력첨부(파일) 업로드/조회/삭제 서비스
 - 기능명세: 최대 20개, 10MB 제한, pdf/image만 허용
 - 저장 경로는 app.upload.dir 환경변수로 관리
*/
@Service
@RequiredArgsConstructor
public class ProfileDocumentService {

    private static final long MAX_SIZE = 10L * 1024 * 1024;

    private static final Set<String> ALLOWED = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/webp"
    );

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private final ProfileRepository profileRepository;
    private final ProfileDocumentRepository profileDocumentRepository;

    public List<ProfileDocumentResponse> list(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ProfileErrorCode.PROFILE_NOT_FOUND));

        return profileDocumentRepository.findAllByProfileIdOrderByIdAsc(profile.getId()).stream()
                .map(ProfileDocumentResponse::from)
                .toList();
    }

    @Transactional
    public ProfileDocumentResponse upload(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(GeneralErrorCode.BAD_REQUEST, "파일이 비어있습니다.");
        }

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ProfileErrorCode.PROFILE_NOT_FOUND));

        long count = profileDocumentRepository.countByProfileId(profile.getId());
        if (count >= 20) {
            throw new BusinessException(ProfileErrorCode.DOCUMENT_LIMIT_EXCEEDED);
        }

        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(ProfileErrorCode.FILE_TOO_LARGE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) {
            throw new BusinessException(ProfileErrorCode.FILE_TYPE_NOT_ALLOWED);
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        String savedName = UUID.randomUUID() + ext;

        Path baseDir = Paths.get(uploadDir, "profile-documents").toAbsolutePath().normalize();

        try {
            Files.createDirectories(baseDir);
            Path target = baseDir.resolve(savedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String url = "/uploads/profile-documents/" + savedName;

            ProfileDocument doc = new ProfileDocument(
                    profile.getId(),
                    url,
                    originalName,
                    contentType,
                    file.getSize()
            );

            ProfileDocument saved = profileDocumentRepository.save(doc);
            return ProfileDocumentResponse.from(saved);

        } catch (IOException e) {
            throw new BusinessException(GeneralErrorCode.BAD_REQUEST, "파일 저장에 실패했습니다.");
        }
    }

    @Transactional
    public void delete(Long userId, Long documentId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ProfileErrorCode.PROFILE_NOT_FOUND));

        ProfileDocument doc = profileDocumentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.NOT_FOUND));

        if (!doc.getProfileId().equals(profile.getId())) {
            throw new BusinessException(GeneralErrorCode.FORBIDDEN);
        }

        profileDocumentRepository.delete(doc);

        try {
            String fileUrl = doc.getFileUrl();
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path path = Paths.get(uploadDir, "profile-documents", fileName).toAbsolutePath().normalize();
            Files.deleteIfExists(path);
        } catch (Exception ignored) {
        }
    }
}
