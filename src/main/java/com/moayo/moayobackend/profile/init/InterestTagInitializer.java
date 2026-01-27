package com.moayo.moayobackend.profile.init;

import com.moayo.moayobackend.profile.entity.InterestTag;
import com.moayo.moayobackend.profile.repository.InterestTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 InterestTagInitializer
 - 관심태그 초기 데이터 삽입
 - 태그는 유저가 생성하지 않고 사전 정의 목록에서 선택
*/
@Component
@RequiredArgsConstructor
public class InterestTagInitializer implements CommandLineRunner {

    private final InterestTagRepository interestTagRepository;

    @Override
    public void run(String... args) {
        if (interestTagRepository.count() > 0) return;

        List<String> tags = List.of("기획", "마케팅", "디자인", "개발", "창업", "예체능", "문학", "기타");
        interestTagRepository.saveAll(tags.stream().map(InterestTag::new).toList());
    }
}
