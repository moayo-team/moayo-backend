package com.moayo.moayobackend.profile.service;

import com.moayo.moayobackend.profile.dto.response.UserInterestTagResponse;
import com.moayo.moayobackend.profile.entity.UserInterestTag;
import com.moayo.moayobackend.profile.repository.UserInterestTagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserInterestTagService {

    private final UserInterestTagRepository userInterestTagRepository;
    private final InterestTagService interestTagService;

    public List<UserInterestTagResponse> findMine(Long userId) {
        return userInterestTagRepository.findByUserId(userId)
                .stream()
                .map(uit -> new UserInterestTagResponse(uit.getId(), uit.getInterestTagId()))
                .toList();
    }

    // 태그 개수 제한 없음. 요청 값으로 전체 교체
    public void replace(Long userId, List<Long> tagIds) {
        if (tagIds == null) tagIds = List.of();

        // 유효성 검증
        interestTagService.validateAllExist(tagIds);

        userInterestTagRepository.deleteByUserId(userId);
        for (Long tagId : tagIds) {
            userInterestTagRepository.save(UserInterestTag.create(userId, tagId));
        }
    }
}
