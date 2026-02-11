package com.moayo.moayobackend.experience.dto.request;

public record ExperienceAiDraftRequest(
        String title,               // 활동명
        String organization,        // 주최/기관
        String startDate,           // 기간 시작(문자열)
        String endDate,             // 기간 종료(없을 수 있음)
        String participationType,   // 참여형태
        String role,                // 역할(없을 수 있음)
        String draftText            // 사용자가 적은 줄글(대충 쓴 설명)
) {}
