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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileIndexItemService {

    private final ProfileRepository profileRepository;
    private final ProfileIndexItemRepository profileIndexItemRepository;

    // 파일 저장 경로 (아까 생성한 폴더 위치)
    private final String uploadDir = "C:/Project_BSH/moayo-backend/uploads/";

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

        // 1. 최대 4개 제한 체크
        long count = profileIndexItemRepository.countByProfileId(profile.getId());
        if (count >= 4) {
            throw new BusinessException(ProfileErrorCode.INDEX_ITEM_LIMIT_EXCEEDED);
        }

        String finalLinkUrl = req.linkUrl();

        // 2. 타입별 유효성 검사 및 파일 처리
        if (req.itemType() == ProfileIndexItem.ItemType.text) {
            if (req.textValue() == null || req.textValue().isBlank()) {
                throw new BusinessException(GeneralErrorCode.BAD_REQUEST, "text 타입은 textValue가 필요합니다.");
            }
        } else if (req.itemType() == ProfileIndexItem.ItemType.link) {
            if (req.linkUrl() == null || req.linkUrl().isBlank()) {
                throw new BusinessException(GeneralErrorCode.BAD_REQUEST, "link 타입은 linkUrl이 필요합니다.");
            }
        } else if (req.itemType() == ProfileIndexItem.ItemType.file) {
            if (file == null || file.isEmpty()) {
                throw new BusinessException(GeneralErrorCode.BAD_REQUEST, "file 타입은 실제 파일 첨부가 필요합니다.");
            }
            finalLinkUrl = saveFile(file); // 파일 저장 후 경로 반환
        }

        ProfileIndexItem item = new ProfileIndexItem(
                profile.getId(),
                req.indexKey(),
                req.indexValue(),
                req.itemType(),
                req.textValue(),
                finalLinkUrl
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

        String updatedLinkUrl = req.linkUrl();

        // 수정한 항목이 파일 타입이고, 새로운 파일이 들어온 경우 교체
        if (item.getItemType() == ProfileIndexItem.ItemType.file && file != null && !file.isEmpty()) {
            updatedLinkUrl = saveFile(file);
        }

        item.update(req.indexKey(), req.indexValue(), req.textValue(), updatedLinkUrl);
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

    // 로컬 폴더에 파일을 저장하는 헬퍼 메서드
    private String saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File targetFile = new File(uploadDir + fileName);
            file.transferTo(targetFile);
            return "/uploads/" + fileName; // DB에 저장될 경로
        } catch (IOException e) {
            throw new BusinessException(GeneralErrorCode.INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다.");
        }
    }
}