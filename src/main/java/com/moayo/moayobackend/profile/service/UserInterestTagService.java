package com.moayo.moayobackend.profile.service;

import com.moayo.moayobackend.global.exception.BusinessException;
import com.moayo.moayobackend.global.exception.GeneralErrorCode;
import com.moayo.moayobackend.profile.dto.response.InterestTagResponse;
import com.moayo.moayobackend.profile.entity.UserInterestTag;
import com.moayo.moayobackend.profile.exception.ProfileErrorCode;
import com.moayo.moayobackend.profile.repository.InterestTagRepository;
import com.moayo.moayobackend.profile.repository.UserInterestTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 UserInterestTagService
 - 내 관심태그 조회/저장(전체 교체) 로직
 - 태그는 사전 정의 목록만 허용
*/
@Service
@RequiredArgsConstructor
public class UserInterestTagService {

    private final UserInterestTagRepository userInterestTagRepository;
    private final InterestTagRepository interestTagRepository;

    public List<InterestTagResponse> findMine(Long userId) {
        List<Long> tagIds = userInterestTagRepository.findAllByUserId(userId).stream()
                .map(UserInterestTag::getInterestTagId)
                .toList();

        return interestTagRepository.findAllById(tagIds).stream()
                .map(InterestTagResponse::from)
                .toList();
    }

    @Transactional
    public void replace(Long userId, List<Long> tagIds) {
        if (userId == null) {
        throw new BusinessException(GeneralErrorCode.UNAUTHORIZED);
    }
        if (tagIds == null) {
            throw new BusinessException(ProfileErrorCode.TAG_NOT_FOUND, "tagIds는 필수입니다.");
        }

        for (Long id : tagIds) {
            if (id == null || !interestTagRepository.existsById(id)) {
                throw new BusinessException(ProfileErrorCode.TAG_NOT_FOUND);
            }
        }

        userInterestTagRepository.deleteAllByUserId(userId);

        List<UserInterestTag> toSave = tagIds.stream()
                .distinct()
                .map(tagId -> new UserInterestTag(userId, tagId))
                .toList();

        userInterestTagRepository.saveAll(toSave);
    }
}
