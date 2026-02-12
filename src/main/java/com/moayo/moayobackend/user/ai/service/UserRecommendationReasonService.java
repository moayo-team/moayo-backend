package com.moayo.moayobackend.user.ai.service;

import com.moayo.moayobackend.user.ai.entity.JobTag;
import com.moayo.moayobackend.user.entity.User;
import com.moayo.moayobackend.profile.entity.UserInterestTag;
import com.moayo.moayobackend.profile.entity.InterestTag;
import com.moayo.moayobackend.profile.repository.UserInterestTagRepository;
import com.moayo.moayobackend.profile.repository.InterestTagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserRecommendationReasonService {

    private final SynergyMatrix synergyMatrix;
    private final JobTagExtractor jobTagExtractor;
    private final InterestTagMapper interestTagMapper;
    private final UserInterestTagRepository userInterestTagRepository;
    private final InterestTagRepository interestTagRepository;
    private final WebClient webClient;

    public UserRecommendationReasonService(
            SynergyMatrix synergyMatrix,
            JobTagExtractor jobTagExtractor,
            InterestTagMapper interestTagMapper,
            UserInterestTagRepository userInterestTagRepository,
            InterestTagRepository interestTagRepository,
            @Value("${ai.openai.base-url}") String baseUrl,
            @Value("${ai.openai.api-key:}") String apiKey
    ) {
        this.synergyMatrix = synergyMatrix;
        this.jobTagExtractor = jobTagExtractor;
        this.interestTagMapper = interestTagMapper;
        this.userInterestTagRepository = userInterestTagRepository;
        this.interestTagRepository = interestTagRepository;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    /**
     * 추천 사유 생성 메인 로직
     * @param type 프론트에서 넘어온 추천 타입 ("similar" 또는 "synergy")
     */
    public String getMatchReason(User me, User target, String mySnapshot, String targetSnapshot, String type) {
        // 1. 각 유저의 관심 태그 이름 리스트 조회
        List<String> myTagNames = getTagNamesByUserId(me.getId());
        List<String> targetTagNames = getTagNamesByUserId(target.getId());

        // 2. JobTag 추출
        Set<JobTag> myJobs = jobTagExtractor.extract(myTagNames, mySnapshot, interestTagMapper);
        Set<JobTag> targetJobs = jobTagExtractor.extract(targetTagNames, targetSnapshot, interestTagMapper);

        // 3. 시너지 매칭 확인 (SynergyMatrix 활용)
        if (synergyMatrix.synergyScore(myJobs, targetJobs) >= 0.7) {
            String targetJobName = getKoreanJobName(targetJobs);
            return String.format("%s 분야 역량을 보유하여 %s님과 협업 시너지가 기대되는 유저입니다.", targetJobName, me.getName());
        }

        // 4. 공통 관심사 확인
        List<String> commonTags = myTagNames.stream()
                .filter(targetTagNames::contains)
                .collect(Collectors.toList());

        if (!commonTags.isEmpty()) {
            if (commonTags.size() == 1) {
                return String.format("두 분 모두 '%s'에 관심이 있어 대화가 잘 통할 것 같아요!", commonTags.get(0));
            } else {
                // 여러 개일 경우: "Spring, Java 외 n개의 공통 관심사가 있어요!"
                String tagsString = commonTags.stream().limit(2).collect(Collectors.joining(", "));
                int extraCount = commonTags.size() - 2;

                return extraCount > 0
                        ? String.format("두 분 모두 '%s' 외 %d개의 공통 관심사가 있어 이야기가 끊이지 않을 것 같아요!", tagsString, extraCount)
                        : String.format("두 분 모두 '%s'에 공통 관심사가 있어 금방 친해질 수 있을 거예요!", tagsString);
            }
        }

        // 5. 데이터로 설명이 안 될 경우 AI 요약 호출 (OpenAI)
        return fetchAiReason(mySnapshot, targetSnapshot, type);
    }

    private List<String> getTagNamesByUserId(Long userId) {
        List<Long> tagIds = userInterestTagRepository.findAllByUserId(userId).stream()
                .map(UserInterestTag::getInterestTagId)
                .toList();

        return interestTagRepository.findAllById(tagIds).stream()
                .map(InterestTag::getName)
                .collect(Collectors.toList());
    }

    private String getKoreanJobName(Set<JobTag> jobs) {
        JobTag top = jobs.stream().findFirst().orElse(JobTag.ETC);
        return switch (top) {
            case PLANNING -> "기획";
            case DEVELOPMENT -> "개발";
            case DESIGN -> "디자인";
            case MARKETING -> "마케팅";
            case STARTUP -> "창업";
            case ARTS -> "예체능";
            case LITERATURE -> "문학";
            default -> "전문";
        };
    }

    private String fetchAiReason(String mySnapshot, String targetSnapshot, String type) {
        try {
            // 타입에 따른 페르소나 설정
            String persona = type.equals("synergy")
                    ? "비즈니스 파트너 매칭 전문가"
                    : "커리어 네트워킹 가이드";

            String goal = type.equals("synergy")
                    ? "서로 다른 강점이 어떻게 보완될지 강조하세요."
                    : "비슷한 고민이나 관심사를 가진 동료임을 강조하세요.";

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o-mini",
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    String.format("당신은 %s입니다. 두 유저의 정보를 분석하여 추천 사유를 한 문장(35자 이내)으로 작성하세요. " +
                                            "조건: 1. %s 2. '~할 것 같아요'나 '~이 기대돼요' 같은 따뜻한 말투 사용 3. 전문용어를 적절히 섞어 신뢰감을 줄 것.", persona, goal)),
                            Map.of("role", "user", "content", "내 정보: " + mySnapshot + "\n상대 정보: " + targetSnapshot)
                    ),
                    "max_tokens", 100,
                    "temperature", 0.8 // 다양성을 위해 랜덤성 부여
            );

            return webClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(response -> {
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return (String) message.get("content");
                    })
                    .block(Duration.ofSeconds(5));
        } catch (Exception e) {
            log.warn("AI 추천 사유 생성 실패: {}", e.getMessage());
            return "함께 성장할 수 있는 멋진 동료를 찾았어요!"; // Fallback
        }
    }

    private record AiRequest(String prompt, String context) {}
}