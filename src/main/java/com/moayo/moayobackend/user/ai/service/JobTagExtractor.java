package com.moayo.moayobackend.user.ai.service;

import com.moayo.moayobackend.user.ai.entity.JobTag;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobTagExtractor {

    public Set<JobTag> extract(List<String> interestTagNames, String snapshotText, InterestTagMapper mapper) {
        EnumSet<JobTag> tags = EnumSet.noneOf(JobTag.class);

        // 1) 사용자가 선택한 관심태그를 최우선 반영
        if (interestTagNames != null) {
            for (String name : interestTagNames) {
                tags.add(mapper.map(name));
            }
        }

        // 2) 태그가 없다면 스냅샷 텍스트에서 보강 추출
        String t = (snapshotText == null ? "" : snapshotText).toLowerCase(Locale.ROOT);

        if (tags.isEmpty()) {
            if (containsAny(t, "기획", "pm", "요구사항", "스토리보드", "프로덕트")) tags.add(JobTag.PLANNING);
            if (containsAny(t, "마케팅", "퍼포먼스", "sns", "브랜드", "광고")) tags.add(JobTag.MARKETING);
            if (containsAny(t, "디자인", "ux", "ui", "figma", "브랜딩")) tags.add(JobTag.DESIGN);
            if (containsAny(t, "개발", "spring", "react", "api", "서버", "앱", "db", "mysql")) tags.add(JobTag.DEVELOPMENT);
            if (containsAny(t, "창업", "사업", "mvp", "피벗", "투자", "bm")) tags.add(JobTag.STARTUP);
            if (containsAny(t, "예체능", "음악", "미술", "체육", "공연")) tags.add(JobTag.ARTS);
            if (containsAny(t, "문학", "소설", "시", "에세이", "글쓰기")) tags.add(JobTag.LITERATURE);
        } else {
            // 선택 태그가 있어도, 스냅샷에서 강하게 드러나는 보강은 추가(선택)
            if (!tags.contains(JobTag.STARTUP) && containsAny(t, "창업", "사업", "mvp", "피벗", "투자", "bm")) {
                tags.add(JobTag.STARTUP);
            }
        }

        if (tags.isEmpty()) tags.add(JobTag.ETC);
        return tags;
    }

    private boolean containsAny(String t, String... keywords) {
        for (String k : keywords) {
            if (t.contains(k.toLowerCase(Locale.ROOT))) return true;
        }
        return false;
    }
}
