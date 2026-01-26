package com.moayo.moayobackend.profile.service;

import com.moayo.moayobackend.profile.dto.request.ProfileCreateRequest;
import com.moayo.moayobackend.profile.dto.request.ProfileUpdateRequest;
import com.moayo.moayobackend.profile.dto.response.ProfileMeResponse;
import com.moayo.moayobackend.profile.dto.response.ProfileUserResponse;
import com.moayo.moayobackend.profile.entity.Profile;
import com.moayo.moayobackend.profile.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileMeResponse getMyProfile(Long userId) {
        Profile p = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));
        return new ProfileMeResponse(p.getId(), p.getUserId(), p.getImageUrl(), p.getBio(), p.getUniversity(), p.getMajor());
    }

    public ProfileUserResponse getOtherProfile(Long userId) {
        Profile p = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));
        return new ProfileUserResponse(p.getId(), p.getUserId(), p.getImageUrl(), p.getBio(), p.getUniversity(), p.getMajor());
    }

    // 생성은 1회만 허용
    public void create(Long userId, ProfileCreateRequest req) {
        if (profileRepository.existsByUserId(userId)) {
            throw new IllegalStateException("이미 프로필이 존재합니다.");
        }
        // ERD 정책: bio/university/major는 필수. 여기서는 단순 검증(팀 예외처리 규칙 맞추면 커스텀 예외로 교체)
        if (isBlank(req.bio()) || isBlank(req.university()) || isBlank(req.major())) {
            throw new IllegalArgumentException("bio/university/major는 필수입니다.");
        }

        Profile p = Profile.create(userId, req.bio(), req.university(), req.major(), req.imageUrl());
        profileRepository.save(p);
    }

    // 부분 수정
    public void update(Long userId, ProfileUpdateRequest req) {
        Profile p = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));

        // 필수 필드 정책을 유지하려면, 들어온 값이 빈 문자열이면 막는 게 안전
        if (req.bio() != null && isBlank(req.bio())) throw new IllegalArgumentException("bio는 비울 수 없습니다.");
        if (req.university() != null && isBlank(req.university())) throw new IllegalArgumentException("university는 비울 수 없습니다.");
        if (req.major() != null && isBlank(req.major())) throw new IllegalArgumentException("major는 비울 수 없습니다.");

        p.update(req.bio(), req.university(), req.major(), req.imageUrl());
    }

    public Long getMyProfileId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."))
                .getId();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
