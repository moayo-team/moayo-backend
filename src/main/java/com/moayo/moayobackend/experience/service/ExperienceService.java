package com.moayo.moayobackend.experience.service;

import com.moayo.moayobackend.experience.dto.request.ExperienceAiDraftRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceCreateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceUpdateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceVisibilityRequest;
import com.moayo.moayobackend.experience.dto.response.ExperienceAiDraftResponse;
import com.moayo.moayobackend.experience.dto.response.ExperienceDetailResponse;
import com.moayo.moayobackend.experience.dto.response.ExperienceSummaryResponse;
import com.moayo.moayobackend.experience.entity.Experience;
import com.moayo.moayobackend.experience.entity.ExperienceFile;
import com.moayo.moayobackend.experience.entity.ExperienceLink;
import com.moayo.moayobackend.experience.repository.ExperienceFileRepository;
import com.moayo.moayobackend.experience.repository.ExperienceLinkRepository;
import com.moayo.moayobackend.experience.repository.ExperienceRepository;
import com.moayo.moayobackend.experience.service.AiServerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final AiServerClient aiServerClient;
    private final ExperienceFileRepository experienceFileRepository;
    private final ExperienceLinkRepository experienceLinkRepository;

    @Transactional(readOnly = true)
    public List<ExperienceSummaryResponse> listMyExperiences(Long userId) {
        return experienceRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(e -> new ExperienceSummaryResponse(
                        e.getId(),
                        e.getOrganization(),
                        e.getTitle(),
                        e.getStartDate(),
                        e.getEndDate(),
                        e.getActivity(),
                        e.getRole(),
                        e.getVisible()
                ))
                .toList();
    }

    @Transactional
    public Long create(Long userId, ExperienceCreateRequest req) {
        Experience e = new Experience(
                userId,
                req.organization(),
                req.title(),
                req.activity(),
                req.role(),
                req.summary(),
                req.startDate(),
                req.endDate(),
                true // 생성 시 기본 공개(true)
        );
        return experienceRepository.save(e).getId();
    }

    @Transactional(readOnly = true)
    public ExperienceDetailResponse getMyDetail(Long userId, Long experienceId) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        return new ExperienceDetailResponse(
                e.getId(),
                e.getOrganization(),
                e.getTitle(),
                e.getStartDate(),
                e.getEndDate(),
                e.getActivity(),
                e.getRole(),
                e.getSummary(),
                e.getVisible()
        );
    }

    @Transactional(readOnly = true)
    public ExperienceDetailResponse getPublicDetail(Long experienceId) {
        Experience e = experienceRepository.findByIdAndVisibleTrue(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found or not public"));

        return new ExperienceDetailResponse(
                e.getId(),
                e.getOrganization(),
                e.getTitle(),
                e.getStartDate(),
                e.getEndDate(),
                e.getActivity(),
                e.getRole(),
                e.getSummary(),
                e.getVisible()
        );
    }

    @Transactional
    public void update(Long userId, Long experienceId, ExperienceUpdateRequest req) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        e.applyPatch(
                req.organization(),
                req.title(),
                req.activity(),
                req.role(),
                req.summary(),
                req.startDate(),
                req.endDate()
        );
    }

    @Transactional
    public void delete(Long userId, Long experienceId) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        experienceRepository.delete(e);
    }

    @Transactional
    public void changeVisibility(Long userId, Long experienceId, ExperienceVisibilityRequest req) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        e.changeVisibility(req.visible());
    }

    // 타인(프로필) 공개 이력 조회
    @Transactional(readOnly = true)
    public List<ExperienceSummaryResponse> listPublicByUser(Long userId) {
        return experienceRepository.findAllByUserIdAndVisibleTrueOrderByCreatedAtDesc(userId).stream()
                .map(e -> new ExperienceSummaryResponse(
                        e.getId(),
                        e.getOrganization(),
                        e.getTitle(),
                        e.getStartDate(),
                        e.getEndDate(),
                        e.getActivity(),
                        e.getRole(),
                        e.getVisible()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ExperienceAiDraftResponse draftWithAi(Long userId, Long experienceId, ExperienceAiDraftRequest req) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        List<ExperienceFile> files =
                experienceFileRepository.findAllByExperience_IdOrderByCreatedAtDesc(experienceId);
        List<ExperienceLink> links =
                experienceLinkRepository.findAllByExperience_IdOrderByCreatedAtDesc(experienceId);

        // 사용자가 추가로 던진 프롬프트(선택)
        String userPrompt = (req == null || req.prompt() == null) ? "" : req.prompt();

        // OpenAI로 보내는 최종 프롬프트(컨텍스트 포함)
        String context = buildContext(e, files, links);
        String finalPrompt = buildFinalPrompt(userPrompt, context);

        // 내부 AI 서버 호출 (prompt + context)
        return aiServerClient.generateExperienceDraft(finalPrompt, context);
    }

    private String buildFinalPrompt(String userPrompt, String context) {
        // 필요하면 여기에서 톤, 형식 요구사항을 강제할 수 있음
        return """
        너는 채용 담당자가 읽기 좋은 이력서 문장을 작성하는 전문가야.
        아래 [Existing Experience], [Attached Files], [Attached Links] 내용을 바탕으로
        한국어로 자연스럽고 간결하게 이력서 문구 초안을 작성해줘.
        - 과장 없이 사실 기반으로
        - 성과/역할/기술이 드러나게
        - 결과는 반드시 불릿 3~5개로만 출력
        - 각 줄은 "• " 로 시작
        - 이모지/따옴표/코드블록 금지
<<<<<<< HEAD

        [User Prompt]
        %s

=======
        [User Prompt]
        %s

>>>>>>> 6c2a497186292e750354360c4640bdcc63931362
        %s
        """.formatted(userPrompt == null ? "" : userPrompt, context == null ? "" : context);
    }

    private String buildContext(Experience e, List<ExperienceFile> files, List<ExperienceLink> links) {
        String filePart = files.stream()
                .limit(5)
                .map(f -> "- " + safe(f.getFileName()) + " (fileId=" + f.getFileId() + ")")
                .reduce("", (a, b) -> a + "\n" + b);

        String linkPart = links.stream()
                .limit(5)
                .map(l -> "- " + safe(l.getTitle()) + " : " + safe(l.getUrl()))
                .reduce("", (a, b) -> a + "\n" + b);

        return """
        [Existing Experience]
        organization=%s
        title=%s
        activity=%s
        role=%s
        summary=%s
        startDate=%s
        endDate=%s

        [Attached Files]
        %s

        [Attached Links]
        %s
        """.formatted(
                safe(e.getOrganization()),
                safe(e.getTitle()),
                safe(e.getActivity()),
                safe(e.getRole()),
                safe(e.getSummary()),
                e.getStartDate(),
                e.getEndDate(),
                filePart.isBlank() ? "(none)" : filePart,
                linkPart.isBlank() ? "(none)" : linkPart
        );
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }
}
