package com.moayo.moayobackend.profile.init;

import com.moayo.moayobackend.profile.entity.InterestTag;
import com.moayo.moayobackend.profile.repository.InterestTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
// 관심 태그
public class InterestTagInitializer {

    private final InterestTagRepository interestTagRepository;

    @PostConstruct
    public void init() {
        // 이미 데이터가 있으면 아무 것도 하지 않음
        if (interestTagRepository.count() > 0) return;

        List<String> tags = List.of(
                "기획", "마케팅", "디자인", "개발",
                "창업", "예체능", "문학", "기타"
        );

        for (String name : tags) {
            // 이론상 count()==0이면 중복이 없지만, 안전하게 체크
            interestTagRepository.findByName(name)
                    .orElseGet(() -> interestTagRepository.save(new InterestTag(name)));
        }
    }
}
