package com.moayo.moayobackend.user.service;

import com.moayo.moayobackend.experience.entity.Experience;
import com.moayo.moayobackend.experience.repository.ExperienceRepository;
import com.moayo.moayobackend.profile.entity.InterestTag;
import com.moayo.moayobackend.profile.entity.Profile;
import com.moayo.moayobackend.profile.entity.UserInterestTag;
import com.moayo.moayobackend.profile.repository.InterestTagRepository;
import com.moayo.moayobackend.profile.repository.ProfileRepository;
import com.moayo.moayobackend.profile.repository.UserInterestTagRepository;
import com.moayo.moayobackend.user.ai.entity.UserProfileSnapshot;
import com.moayo.moayobackend.user.ai.repository.UserProfileSnapshotRepository;
import com.moayo.moayobackend.user.entity.User;
import com.moayo.moayobackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileSnapshotService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ExperienceRepository experienceRepository;
    private final UserInterestTagRepository userInterestTagRepository;
    private final InterestTagRepository interestTagRepository;
    private final UserProfileSnapshotRepository snapshotRepository;

    @Transactional
    public UserProfileSnapshot rebuildSnapshot(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다. id=" + userId));

        Profile profile = profileRepository.findByUserId(userId).orElse(null);

        // 가시성이 허용된(visible=true) 경력 데이터만 추출
        List<Experience> experiences =
                experienceRepository.findAllByUserIdAndVisibleTrueOrderByCreatedAtDesc(userId);

        List<UserInterestTag> mappings = userInterestTagRepository.findAllByUserId(userId);
        Set<Long> tagIds = mappings.stream().map(UserInterestTag::getInterestTagId).collect(Collectors.toSet());
        List<String> interestTagNames = interestTagIdsToNames(tagIds);

        String snapshotText = buildText(user, profile, interestTagNames, experiences);

        return snapshotRepository.findById(userId)
                .map(s -> {
                    s.updateText(snapshotText);
                    return s;
                })
                .orElseGet(() -> snapshotRepository.save(UserProfileSnapshot.of(userId, snapshotText)));
    }

    private List<String> interestTagIdsToNames(Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return List.of();
        return interestTagRepository.findAllById(tagIds).stream()
                .map(InterestTag::getName)
                .toList();
    }

    private String buildText(User user,
                             Profile profile,
                             List<String> interestTagNames,
                             List<Experience> exps) {

        StringBuilder sb = new StringBuilder();

        // 1. 기초 정보
        sb.append("[Basic Identity]\n");
        sb.append("Name: ").append(nullSafe(user.getName())).append("\n");

        // 2. 관심사
        sb.append("[Interests]\n");
        sb.append("Tags: ").append(String.join(", ", interestTagNames)).append("\n");

        // 3. 프로필 상세
        if (profile != null) {
            sb.append("[Profile Details]\n");
            sb.append("Introduction: ").append(nullSafe(profile.getBio())).append("\n");
            sb.append("Education: ").append(nullSafe(profile.getUniversity()))
                    .append(" (").append(nullSafe(profile.getMajor())).append(")\n");
        }

        // 4. 경력 및 경험
        sb.append("[Detailed Experiences]\n");
        for (Experience e : exps) {
            sb.append("- Organization: ").append(nullSafe(e.getOrganization())).append("\n");
            sb.append("  Activity: ").append(nullSafe(e.getActivity()))
                    .append(" | Title: ").append(nullSafe(e.getTitle())).append("\n");
            sb.append("  Role: ").append(nullSafe(e.getRole())).append("\n");
            sb.append("  Period: ").append(e.getStartDate()).append(" ~ ").append(e.getEndDate()).append("\n");
            sb.append("  Summary: ").append(nullSafe(e.getSummary())).append("\n\n");
        }

        return sb.toString();
    }

    private String nullSafe(String s) {
        return (s == null || s.isBlank()) ? "None" : s;
    }
}