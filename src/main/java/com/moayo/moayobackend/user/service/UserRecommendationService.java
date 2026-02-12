package com.moayo.moayobackend.user.service;

import com.moayo.moayobackend.profile.repository.ProfileRepository;
import com.moayo.moayobackend.profile.repository.UserInterestTagRepository;
import com.moayo.moayobackend.profile.repository.InterestTagRepository;
import com.moayo.moayobackend.user.ai.entity.JobTag;
import com.moayo.moayobackend.user.ai.entity.UserEmbedding;
import com.moayo.moayobackend.user.ai.entity.UserProfileSnapshot;
import com.moayo.moayobackend.user.ai.repository.UserEmbeddingRepository;
import com.moayo.moayobackend.user.ai.repository.UserProfileSnapshotRepository;
import com.moayo.moayobackend.user.ai.service.*;
//import com.moayo.moayobackend.user.ai.service.UserProfileSnapshotService;
import com.moayo.moayobackend.user.dto.RecommendedUserDto;
import com.moayo.moayobackend.user.dto.UserRecommendationResponseDto;
import com.moayo.moayobackend.user.entity.User;
import com.moayo.moayobackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/*
 UserRecommendationService (AI)
 - similar: 임베딩 코사인 유사도 기반 추천
 - synergy: 태그 시너지 점수 + 유사도 혼합 추천
*/
@Service
@RequiredArgsConstructor
public class UserRecommendationService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    private final UserProfileSnapshotRepository snapshotRepository;
    private final UserEmbeddingRepository embeddingRepository;

    private final UserProfileSnapshotService snapshotService;
    private final EmbeddingProvider embeddingProvider;

    private final UserInterestTagRepository userInterestTagRepository;
    private final InterestTagRepository interestTagRepository;

    private final InterestTagMapper interestTagMapper;
    private final JobTagExtractor jobTagExtractor;
    private final SynergyMatrix synergyMatrix;
    private final UserRecommendationReasonService reasonService;


    // 추천 진입점
    @Transactional
    public UserRecommendationResponseDto recommend(Long userId, String type, int limit) {

        // 1) 본인 확인
        User me = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다. id=" + userId));

        // 2) 본인 스냅샷/임베딩 준비
        UserProfileSnapshot meSnapshot = snapshotRepository.findById(userId)
                .orElseGet(() -> snapshotService.rebuildSnapshot(userId));

        UserEmbedding meEmbedding = upsertEmbedding(userId, meSnapshot.getText());
        List<Double> meVec = SimilarityUtils.fromJson(meEmbedding.getVectorJson());

        // 3) 본인 관심태그 이름 목록 + 태그 추출
        List<String> myInterestNames = loadInterestTagNames(userId);
        Set<JobTag> myTags = jobTagExtractor.extract(myInterestNames, meSnapshot.getText(), interestTagMapper);

        // 4) 후보군(최근 300명) 로드
        List<User> candidates = userRepository.findByIdNotOrderByCreatedAtDesc(userId, PageRequest.of(0, 300));

        // 5) 후보 점수 계산
        List<ScoredCandidate> scored = new ArrayList<>();
        boolean synergyMode = "synergy".equalsIgnoreCase(type);

        Map<Long, String> snapshotCache = new HashMap<>();
        snapshotCache.put(userId, meSnapshot.getText());

        for (User c : candidates) {
            Long cid = c.getId();

            UserProfileSnapshot cs = snapshotRepository.findById(cid)
                    .orElseGet(() -> snapshotService.rebuildSnapshot(cid));

            snapshotCache.put(cid, cs.getText());
            UserEmbedding ce = upsertEmbedding(cid, cs.getText());
            double sim = SimilarityUtils.cosine(meVec, SimilarityUtils.fromJson(ce.getVectorJson()));

            if (synergyMode) {
                List<String> cInterestNames = loadInterestTagNames(cid);
                Set<JobTag> cTags = jobTagExtractor.extract(cInterestNames, cs.getText(), interestTagMapper);

                double syn = synergyMatrix.synergyScore(myTags, cTags);
                double finalScore = (0.6 * syn) + (0.4 * sim);
                scored.add(new ScoredCandidate(c, sim, syn, finalScore));
            } else {
                scored.add(new ScoredCandidate(c, sim, 0.0, sim));
            }
        }

        scored.sort(Comparator.comparingDouble(ScoredCandidate::finalScore).reversed());
        List<ScoredCandidate> top = scored.stream().limit(limit).toList();

        // 6) DTO 변환 (profile imageUrl 붙이기)
        List<RecommendedUserDto> items = top.stream()
                .map(sc -> {
                    User target = sc.user();
                    String imageUrl = profileRepository.findByUserId(target.getId())
                            .map(p -> p.getImageUrl())
                            .orElse(null);

                    String bio = toBio(loadInterestTagNames(target.getId()));

                    String reason = reasonService.getMatchReason(
                            me,
                            target,
                            snapshotCache.get(me.getId()),
                            snapshotCache.get(target.getId()),
                            type
                    );

                    return RecommendedUserDto.builder()
                            .userId(target.getId())
                            .name(target.getName())
                            .imageUrl(imageUrl)
                            .bio(bio)
                            .matchReason(reason)
                            .build();
                })
                .collect(Collectors.toList());

        return UserRecommendationResponseDto.builder()
                .type(synergyMode ? "synergy" : "similar")
                .items(items)
                .build();
    }

    /*
     관심태그 이름 로드
     - user_interest_tags(userId, interestTagId) -> interest_tags(id,name)
    */
    private List<String> loadInterestTagNames(Long userId) {
        var mappings = userInterestTagRepository.findAllByUserId(userId);
        if (mappings.isEmpty()) return List.of();

        var ids = mappings.stream().map(m -> m.getInterestTagId()).collect(Collectors.toSet());
        if (ids.isEmpty()) return List.of();

        return interestTagRepository.findAllById(ids).stream()
                .map(t -> t.getName())
                .toList();
    }

    private String toBio(List<String> interestNames) {
        if (interestNames == null || interestNames.isEmpty()) return "모아요 추천 유저";

        return interestNames.stream().limit(3).collect(Collectors.joining(", "));
    }

    private UserEmbedding upsertEmbedding(Long userId, String snapshotText) {
        List<Double> vec;

        try {
            vec = embeddingProvider.embed(snapshotText);
        } catch (Exception e) {
            System.out.println("AI 임베딩 생성 실패 (유저 ID: " + userId + ")\n 에러 내용 : " + e.getMessage());
            vec = new ArrayList<>(Collections.nCopies(embeddingProvider.dimension(), 0.0));
        }

        String json = SimilarityUtils.toJson(vec);

        return embeddingRepository.findById(userId)
                .map(e -> { e.update(embeddingProvider.dimension(), json); return e; })
                .orElseGet(() -> embeddingRepository.save(UserEmbedding.of(userId, embeddingProvider.dimension(), json)));
    }

    private record ScoredCandidate(User user, double similarity, double synergy, double finalScore) {}
}
