package com.moayo.moayobackend.profile.service;

import com.moayo.moayobackend.global.exception.BusinessException;
import com.moayo.moayobackend.global.exception.GeneralErrorCode;
import com.moayo.moayobackend.profile.dto.request.ProfileIndexItemCreateRequest;
import com.moayo.moayobackend.profile.dto.request.ProfileIndexItemUpdateRequest;
import com.moayo.moayobackend.profile.dto.response.ProfileIndexItemResponse;
import com.moayo.moayobackend.profile.entity.Profile;
import com.moayo.moayobackend.profile.entity.ProfileIndexItem;
import com.moayo.moayobackend.profile.exception.ProfileErrorCode;
import com.moayo.moayobackend.profile.repository.ProfileIndexItemRepository;
import com.moayo.moayobackend.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileIndexItemService {

    private final ProfileRepository profileRepository;
    private final ProfileIndexItemRepository profileIndexItemRepository;

    @Value("${app.upload.dir:/uploads/}")
    private String uploadDir;

    @Transactional(readOnly = true)
    public List<ProfileIndexItemResponse> findMine(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ProfileErrorCode.PROFILE_NOT_FOUND));

        return profileIndexItemRepository.findAllByProfileIdOrderByIdAsc(profile.getId()).stream()
                .map(ProfileIndexItemResponse::from)
                .toList();
    }

    @Transactional
    public void create(Long userId, ProfileIndexItemCreateRequest req, MultipartFile file) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ProfileErrorCode.PROFILE_NOT_FOUND));

        String processedValue = req.indexValue();
        String processedLink = req.linkUrl();

        validateData(req.itemType(), req.indexKey(), processedValue, processedLink, file);

        if (req.itemType() == ProfileIndexItem.ItemType.file) {
            processedLink = saveFile(file);
            if (!StringUtils.hasText(processedValue)) {
                processedValue = file.getOriginalFilename(); // 파일명이 제목 미입력 시 원본파일명 사용
            }
        } else if (req.itemType() == ProfileIndexItem.ItemType.link) {
            if (!StringUtils.hasText(processedValue)) {
                processedValue = processedLink;
            }
        }

        ProfileIndexItem item = new ProfileIndexItem(
                profile.getId(),
                req.indexKey(),
                (processedValue != null) ? processedValue : "",
                req.itemType(),
                processedLink
        );

        profileIndexItemRepository.save(item);
    }

    @Transactional
    public void update(Long userId, Long itemId, ProfileIndexItemUpdateRequest req, MultipartFile file) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ProfileErrorCode.PROFILE_NOT_FOUND));

        ProfileIndexItem item = profileIndexItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.NOT_FOUND));

        if (!item.getProfileId().equals(profile.getId())) {
            throw new BusinessException(GeneralErrorCode.FORBIDDEN);
        }

        String updatedLink = req.linkUrl();
        String updatedValue = req.indexValue();

        if (req.itemType() == ProfileIndexItem.ItemType.file && file != null && !file.isEmpty()) {
            updatedLink = saveFile(file);
            if (!StringUtils.hasText(updatedValue)) {
                updatedValue = file.getOriginalFilename();
            }
        }
        else if (req.itemType() == ProfileIndexItem.ItemType.link) {
            if (!StringUtils.hasText(updatedValue)) {
                updatedValue = updatedLink;
            }
        }

        item.update(req.indexKey(), updatedValue, req.itemType(), updatedLink);
    }

    @Transactional
    public void delete(Long userId, Long itemId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ProfileErrorCode.PROFILE_NOT_FOUND));

        ProfileIndexItem item = profileIndexItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.NOT_FOUND));

        if (!item.getProfileId().equals(profile.getId())) {
            throw new BusinessException(GeneralErrorCode.FORBIDDEN);
        }

        profileIndexItemRepository.delete(item);
    }

    private void validateData(ProfileIndexItem.ItemType type, String key, String value, String link, MultipartFile file) {
        if (!StringUtils.hasText(key)) {
            throw new BusinessException(GeneralErrorCode.BAD_REQUEST, "제목은 필수 입력 사항입니다.");
        }

        switch (type) {
            case text -> {
                if (!StringUtils.hasText(value)) {
                    throw new BusinessException(GeneralErrorCode.BAD_REQUEST, "내용을 입력해주세요.");
                }
            }
            case link -> {
                if (!StringUtils.hasText(link)) {
                    throw new BusinessException(GeneralErrorCode.BAD_REQUEST, "링크 주소를 입력해주세요.");
                }
            }
            case file -> {
                if (file == null || file.isEmpty()) {
                    throw new BusinessException(GeneralErrorCode.BAD_REQUEST, "파일을 첨부해주세요.");
                }
            }
        }
    }

    private String saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path targetPath = Paths.get(uploadDir).resolve(fileName).normalize();

            if (!Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
            }

            file.transferTo(targetPath.toFile());

            log.info("파일 저장 완료: {}", targetPath);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            log.error("파일 저장 실패: ", e);
            throw new BusinessException(GeneralErrorCode.INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다.");
        }
    }
}