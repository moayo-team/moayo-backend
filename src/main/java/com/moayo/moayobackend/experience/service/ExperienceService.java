package com.moayo.moayobackend.experience.service;

import com.moayo.moayobackend.experience.dto.request.ExperienceAiDraftRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceCreateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceUpdateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceVisibilityRequest;
import com.moayo.moayobackend.experience.dto.response.ExperienceAiDraftResponse;
import com.moayo.moayobackend.experience.dto.response.ExperienceDetailResponse;
import com.moayo.moayobackend.experience.dto.response.ExperienceSummaryResponse;
import com.moayo.moayobackend.experience.entity.Experience;
import com.moayo.moayobackend.experience.repository.ExperienceFileRepository;
import com.moayo.moayobackend.experience.repository.ExperienceLinkRepository;
import com.moayo.moayobackend.experience.repository.ExperienceRepository;
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
                        e.getSummary(),
                        e.getVisible()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ExperienceAiDraftResponse draftWithAi(Long userId, Long experienceId, ExperienceAiDraftRequest req) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        // 첨부파일, 링크를 context로 합쳐 AI 품질 올리기
        var files = experienceFileRepository.findAllByExperience_IdOrderByCreatedAtDesc(experienceId);
        var links = experienceLinkRepository.findAllByExperience_IdOrderByCreatedAtDesc(experienceId);

        String context = buildContext(e, files, links);
        String prompt = (req == null || req.prompt() == null) ? "" : req.prompt();

        // 여기서만 AI 서버 호출, 그리고 결과를 그대로 반환(저장 X)
        return aiServerClient.generateExperienceDraft(prompt, context);
    }

    private String buildContext(Experience e,
                                List<com.moayo.moayobackend.experience.entity.ExperienceFile> files,
                                List<com.moayo.moayobackend.experience.entity.ExperienceLink> links) {

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