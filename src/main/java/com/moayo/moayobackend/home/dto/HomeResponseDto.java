package com.moayo.moayobackend.home.dto;

import com.moayo.moayobackend.post.dto.PostResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponseDto {

    // 홈 상단/좌측 알림 요약(최근 일주일 내 안 읽은 쪽지 수)
    private HomeNotificationSummaryDto notifications;

    // 홈 중앙 마감 임박 게시글 카드용 데이터
    private List<PostResponseDto> imminentPosts;

    // 홈 우측 추천 유저 카드 데이터
    private List<HomeRecommendedUserDto> recommendedUsers;
}
