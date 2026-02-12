package com.moayo.moayobackend.experience.service;

import com.moayo.moayobackend.global.ai.OpenAiClient;
import com.moayo.moayobackend.experience.dto.request.ExperienceAiDraftRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceCreateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceUpdateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceVisibilityRequest;
import com.moayo.moayobackend.experience.dto.response.ExperienceAiDraftResponse;
import com.moayo.moayobackend.experience.dto.response.ExperienceDetailResponse;
import com.moayo.moayobackend.experience.dto.response.ExperienceSummaryResponse;
import com.moayo.moayobackend.experience.entity.Experience;
import com.moayo.moayobackend.experience.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final OpenAiClient openAiClient;

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
                        e.getSummary(),
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
                true
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
                        e.getSummary(),
                        e.getVisible()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ExperienceAiDraftResponse draftWithAi(ExperienceAiDraftRequest req) {
        String prompt = buildSummaryPrompt(req);
        String summary = openAiClient.draft(prompt);
        return new ExperienceAiDraftResponse(summary);
    }

    private String buildSummaryPrompt(ExperienceAiDraftRequest req) {
        String title = safe(req.title());
        String org = safe(req.organization());
        String start = safe(req.startDate());
        String end = safe(req.endDate());
        String participation = safe(req.participationType());
        String role = safe(req.role());
        String userDraft = safe(req.draftText());

        String period = end.isBlank() ? start : (start + " ~ " + end);

        return """
        너는 채용 담당자가 읽기 좋은 "활동 소개" 문장을 작성하는 전문가야.

        아래 정보를 바탕으로 "활동 소개(summary)"를 한국어로 자연스럽게 작성해줘.
        - 과장하지 말고 입력된 사실에 기반해서 정제
        - 3~5문장 정도의 자연스러운 줄글
        - 기술/역할/성과가 드러나게 (없으면 억지로 만들지 말기)
        - 특정 수치/성과는 사용자가 말한 것만 사용
        - 출력은 오직 활동 소개 본문만(제목/불릿/머리말/따옴표/이모지 금지)

        [활동명]
        %s

        [주최/기관]
        %s

        [기간]
        %s

        [참여형태]
        %s

        [역할]
        %s

        [사용자 작성 내용]
        %s
        """.formatted(title, org, period, participation, role, userDraft);
    }

    private String safe(String v) {
        return v == null ? "" : v.trim();
    }
}
