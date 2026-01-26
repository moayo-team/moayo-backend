package com.moayo.moayobackend.profile.service;

import com.moayo.moayobackend.profile.dto.request.ProfileIndexItemCreateRequest;
import com.moayo.moayobackend.profile.dto.request.ProfileIndexItemUpdateRequest;
import com.moayo.moayobackend.profile.dto.response.ProfileIndexItemResponse;
import com.moayo.moayobackend.profile.entity.ProfileIndexItem;
import com.moayo.moayobackend.profile.repository.ProfileIndexItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileIndexItemService {

    private final ProfileService profileService;
    private final ProfileIndexItemRepository profileIndexItemRepository;

    public void create(Long userId, ProfileIndexItemCreateRequest req) {
        Long profileId = profileService.getMyProfileId(userId);

        // 캡쳐 API 범위 기준: index-items 개수 제한은 강제하지 않음
        validateCommon(req.indexKey(), req.indexValue(), req.itemType());
        validateItemType(req.itemType(), req.textValue(), req.linkUrl(), req.fileUrl());

        ProfileIndexItem item = ProfileIndexItem.create(
                profileId,
                req.indexKey(),
                req.indexValue(),
                req.itemType(),
                req.textValue(),
                req.linkUrl(),
                req.fileUrl(),
                req.fileName(),
                req.fileType(),
                req.fileSize()
        );
        profileIndexItemRepository.save(item);
    }

    public List<ProfileIndexItemResponse> findMine(Long userId) {
        Long profileId = profileService.getMyProfileId(userId);
        return profileIndexItemRepository.findByProfileId(profileId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void update(Long userId, Long itemId, ProfileIndexItemUpdateRequest req) {
        Long profileId = profileService.getMyProfileId(userId);

        // 내 소유 item인지 검증
        ProfileIndexItem item = profileIndexItemRepository.findByIdAndProfileId(itemId, profileId)
                .orElseThrow(() -> new IllegalArgumentException("해당 추가 항목이 존재하지 않습니다."));

        // 업데이트 후의 최종값을 기준으로 타입 검증
        String finalItemType = (req.itemType() != null) ? req.itemType() : item.getItemType();
        String finalTextValue = (req.textValue() != null) ? req.textValue() : item.getTextValue();
        String finalLinkUrl = (req.linkUrl() != null) ? req.linkUrl() : item.getLinkUrl();
        String finalFileUrl = (req.fileUrl() != null) ? req.fileUrl() : item.getFileUrl();

        if (req.indexKey() != null && isBlank(req.indexKey())) throw new IllegalArgumentException("indexKey는 비울 수 없습니다.");
        if (req.indexValue() != null && isBlank(req.indexValue())) throw new IllegalArgumentException("indexValue는 비울 수 없습니다.");
        if (req.itemType() != null && isBlank(req.itemType())) throw new IllegalArgumentException("itemType은 비울 수 없습니다.");

        validateItemType(finalItemType, finalTextValue, finalLinkUrl, finalFileUrl);

        item.update(
                req.indexKey(),
                req.indexValue(),
                req.itemType(),
                req.textValue(),
                req.linkUrl(),
                req.fileUrl(),
                req.fileName(),
                req.fileType(),
                req.fileSize()
        );
    }

    public void delete(Long userId, Long itemId) {
        Long profileId = profileService.getMyProfileId(userId);

        ProfileIndexItem item = profileIndexItemRepository.findByIdAndProfileId(itemId, profileId)
                .orElseThrow(() -> new IllegalArgumentException("해당 추가 항목이 존재하지 않습니다."));

        profileIndexItemRepository.delete(item);
    }

    private ProfileIndexItemResponse toResponse(ProfileIndexItem i) {
        return new ProfileIndexItemResponse(
                i.getId(),
                i.getProfileId(),
                i.getIndexKey(),
                i.getIndexValue(),
                i.getItemType(),
                i.getTextValue(),
                i.getLinkUrl(),
                i.getFileUrl(),
                i.getFileName(),
                i.getFileType(),
                i.getFileSize()
        );
    }

    private void validateCommon(String indexKey, String indexValue, String itemType) {
        if (isBlank(indexKey)) throw new IllegalArgumentException("indexKey는 필수입니다.");
        if (isBlank(indexValue)) throw new IllegalArgumentException("indexValue는 필수입니다.");
        if (isBlank(itemType)) throw new IllegalArgumentException("itemType은 필수입니다.");
    }

    private void validateItemType(String itemType, String textValue, String linkUrl, String fileUrl) {
        switch (itemType) {
            case "text" -> {
                if (isBlank(textValue)) throw new IllegalArgumentException("text 타입은 textValue가 필수입니다.");
            }
            case "link" -> {
                if (isBlank(linkUrl)) throw new IllegalArgumentException("link 타입은 linkUrl이 필수입니다.");
            }
            case "file" -> {
                if (isBlank(fileUrl)) throw new IllegalArgumentException("file 타입은 fileUrl이 필수입니다.");
            }
            default -> throw new IllegalArgumentException("itemType은 file/link/text 중 하나여야 합니다.");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
