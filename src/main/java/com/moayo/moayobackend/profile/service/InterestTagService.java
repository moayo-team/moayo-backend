package com.moayo.moayobackend.profile.service;

import com.moayo.moayobackend.profile.dto.response.InterestTagResponse;
import com.moayo.moayobackend.profile.repository.InterestTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestTagService {

    private final InterestTagRepository interestTagRepository;

    public List<InterestTagResponse> findAll() {
        return interestTagRepository.findAll()
                .stream()
                .map(t -> new InterestTagResponse(t.getId(), t.getName()))
                .toList();
    }

    // 존재하지 않는 태그 id가 들어오면 막음 (사전정의 정책 유지)
    public void validateAllExist(List<Long> tagIds) {
        if (tagIds == null) return;
        for (Long id : tagIds) {
            if (id == null || !interestTagRepository.existsById(id)) {
                throw new IllegalArgumentException("유효하지 않은 관심 태그 id가 포함되어 있습니다.");
            }
        }
    }
}
