package com.moayo.moayobackend.profile.service;

import com.moayo.moayobackend.profile.dto.response.InterestTagResponse;
import com.moayo.moayobackend.profile.entity.InterestTag;
import com.moayo.moayobackend.profile.repository.InterestTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 InterestTagService
 - 관심태그 마스터 목록 조회 로직
*/
@Service
@RequiredArgsConstructor
public class InterestTagService {

    private final InterestTagRepository interestTagRepository;

    public List<InterestTagResponse> findAll() {
        return interestTagRepository.findAll().stream()
                .map(InterestTagResponse::from)
                .toList();
    }

    @Transactional
    public InterestTagResponse create(String name) {
        // 이미 존재하는 태그인지 체크 (선택 사항)
        if (interestTagRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 태그입니다.");
        }

        InterestTag tag = new InterestTag(name); // 엔티티 생성자 필요
        InterestTag saved = interestTagRepository.save(tag);
        return InterestTagResponse.from(saved);
    }
}
