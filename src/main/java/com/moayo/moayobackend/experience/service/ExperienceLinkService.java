package com.moayo.moayobackend.experience.service;

import com.moayo.moayobackend.experience.dto.request.ExperienceLinkCreateRequest;
import com.moayo.moayobackend.experience.dto.request.ExperienceLinkUpdateRequest;
import com.moayo.moayobackend.experience.dto.response.ExperienceLinkResponse;
import com.moayo.moayobackend.experience.entity.Experience;
import com.moayo.moayobackend.experience.entity.ExperienceLink;
import com.moayo.moayobackend.experience.repository.ExperienceLinkRepository;
import com.moayo.moayobackend.experience.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceLinkService {

    private final ExperienceRepository experienceRepository;
    private final ExperienceLinkRepository linkRepository;

    @Transactional
    public void create(Long userId, Long experienceId, ExperienceLinkCreateRequest req) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        linkRepository.save(new ExperienceLink(e, req.title(), req.url()));
    }

    @Transactional(readOnly = true)
    public List<ExperienceLinkResponse> list(Long userId, Long experienceId) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        return linkRepository.findAllByExperience_IdOrderByCreatedAtDesc(experienceId).stream()
                .map(l -> new ExperienceLinkResponse(l.getId(), l.getTitle(), l.getUrl()))
                .toList();
    }

    @Transactional
    public void update(Long userId, Long experienceId, Long linkId, ExperienceLinkUpdateRequest req) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        ExperienceLink link = linkRepository.findByIdAndExperience_Id(linkId, experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));

        link.update(req.title(), req.url());
    }

    @Transactional
    public void delete(Long userId, Long experienceId, Long linkId) {
        Experience e = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
        e.validateOwner(userId);

        ExperienceLink link = linkRepository.findByIdAndExperience_Id(linkId, experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));

        linkRepository.delete(link);
    }
}
