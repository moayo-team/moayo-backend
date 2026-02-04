package com.moayo.moayobackend.user.ai.service;

import com.moayo.moayobackend.user.ai.entity.JobTag;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class InterestTagMapper {

    public JobTag map(String interestTagName) {
        if (interestTagName == null) return JobTag.ETC;

        String t = interestTagName.trim().toLowerCase(Locale.ROOT);
        return switch (t) {
            case "기획" -> JobTag.PLANNING;
            case "마케팅" -> JobTag.MARKETING;
            case "디자인" -> JobTag.DESIGN;
            case "개발" -> JobTag.DEVELOPMENT;
            case "창업" -> JobTag.STARTUP;
            case "예체능" -> JobTag.ARTS;
            case "문학" -> JobTag.LITERATURE;
            default -> JobTag.ETC;
        };
    }
}
