package com.moayo.moayobackend.profile.service;

import com.moayo.moayobackend.global.exception.BusinessException;
import com.moayo.moayobackend.global.exception.GeneralErrorCode;
import com.moayo.moayobackend.profile.dto.request.ProfileCreateRequest;
import com.moayo.moayobackend.profile.dto.request.ProfileUpdateRequest;
import com.moayo.moayobackend.profile.dto.response.ProfileDocumentResponse;
import com.moayo.moayobackend.profile.dto.response.ProfileIndexItemResponse;
import com.moayo.moayobackend.profile.dto.response.ProfileMeResponse;
import com.moayo.moayobackend.profile.dto.response.ProfileUserResponse;
import com.moayo.moayobackend.profile.dto.response.InterestTagResponse;
import com.moayo.moayobackend.profile.entity.Profile;
import com.moayo.moayobackend.profile.exception.ProfileErrorCode;
import com.moayo.moayobackend.profile.repository.ProfileRepository;
import com.moayo.moayobackend.user.entity.User;
import com.moayo.moayobackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 ProfileService
 - 프로필 화면 진입 응답 조립
 - 프로필 생성/수정 시 users + profiles를 함께 업데이트
*/
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    private final UserInterestTagService userInterestTagService;
    private final ProfileIndexItemService profileIndexItemService;
    private final ProfileDocumentService profileDocumentService;

    public ProfileMeResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.NOT_FOUND));

        Profile profile = profileRepository.findByUserId(userId).orElse(null);

        List<InterestTagResponse> tags = userInterestTagService.findMine(userId);

        List<ProfileIndexItemResponse> items = (profile == null)
                ? List.of()
                : profileIndexItemService.findMine(userId);

        List<ProfileDocumentResponse> docs = (profile == null)
                ? List.of()
                : profileDocumentService.list(userId);

        ProfileMeResponse.UserPart userPart = new ProfileMeResponse.UserPart(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber()
        );

        ProfileMeResponse.ProfilePart profilePart = (profile == null)
                ? new ProfileMeResponse.ProfilePart(null, null, null, null, null)
                : new ProfileMeResponse.ProfilePart(
                profile.getId(),
                profile.getImageUrl(),
                profile.getUniversity(),
                profile.getMajor(),
                profile.getBio()
        );

        return new ProfileMeResponse(userPart, profilePart, tags, items, docs);
    }

    public ProfileUserResponse getUser(Long targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.NOT_FOUND));

        Profile profile = profileRepository.findByUserId(targetUserId)
                .orElseThrow(() -> new BusinessException(ProfileErrorCode.PROFILE_NOT_FOUND));

        List<InterestTagResponse> tags = userInterestTagService.findMine(targetUserId);
        List<ProfileIndexItemResponse> items = profileIndexItemService.findMine(targetUserId);

        return new ProfileUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                profile.getImageUrl(),
                profile.getUniversity(),
                profile.getMajor(),
                profile.getBio(),
                tags,
                items
        );
    }

    @Transactional
    public void create(Long userId, ProfileCreateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.NOT_FOUND));

        if (profileRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException(GeneralErrorCode.BAD_REQUEST, "이미 프로필이 존재합니다.");
        }

        user.updateBasics(req.name(), req.phoneNumber());

        Profile profile = new Profile(userId, req.imageUrl(), req.bio(), req.university(), req.major());
        profileRepository.save(profile);
    }

    @Transactional
    public void update(Long userId, ProfileUpdateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.NOT_FOUND));

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ProfileErrorCode.PROFILE_NOT_FOUND));

        user.updateBasics(req.name(), req.phoneNumber());
        profile.update(req.imageUrl(), req.bio(), req.university(), req.major());
    }
}
