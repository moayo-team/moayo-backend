package com.moayo.moayobackend.home.service;

import com.moayo.moayobackend.chat.repository.ChatParticipantRepository;
import com.moayo.moayobackend.chat.repository.projection.ChatRoomListItemProjection;
import com.moayo.moayobackend.home.dto.HomeNotificationSummaryDto;
import com.moayo.moayobackend.home.dto.HomeRecommendedUserDto;
import com.moayo.moayobackend.home.dto.HomeResponseDto;
import com.moayo.moayobackend.post.dto.PostResponseDto;
import com.moayo.moayobackend.post.service.PostService;
import com.moayo.moayobackend.user.dto.RecommendedUserDto;
import com.moayo.moayobackend.user.dto.UserRecommendationResponseDto;
import com.moayo.moayobackend.user.service.UserRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final PostService postService;
    private final UserRecommendationService userRecommendationService;
    private final ChatParticipantRepository chatParticipantRepository;

    // 홈 api는 집계 역할, 각 데이터는 각 api에서 끌고옴
    @Transactional(readOnly = true)
    public HomeResponseDto loadHome(Long userId, int postsLimit, int recoLimit, String recoType) {

        // 마감 임박 게시글
        List<PostResponseDto> imminentPosts = postService.getImminentPostsForHome(postsLimit);

        // 알림 요약
        List<ChatRoomListItemProjection> chatRooms = chatParticipantRepository.findChatRoomListByUserId(userId);

        long unreadRoomCount = chatRooms.stream()
                .filter(room -> room.getHasUnread() != null && room.getHasUnread() == 1)
                .count();

        HomeNotificationSummaryDto notifications = HomeNotificationSummaryDto.builder()
                .unreadCount(unreadRoomCount) // 안 읽은 메시지가 있는 방의 개수
                .items(Collections.emptyList()) // 상세 리스트는 쪽지함 API에서 처리
                .build();

        //추천 유저
        UserRecommendationResponseDto reco = userRecommendationService.recommend(userId, recoType, recoLimit);

        // Null 포인트 방지를 위해 stream 호출 전 체크
        List<HomeRecommendedUserDto> recommendedUsers = Collections.emptyList();
        if (reco != null && reco.getItems() != null) {
            recommendedUsers = reco.getItems().stream()
                    .map(this::toHomeRecommendedUserDto)
                    .toList();
        }

        return HomeResponseDto.builder()
                .notifications(notifications)
                .imminentPosts(imminentPosts)
                .recommendedUsers(recommendedUsers)
                .build();
    }

    private HomeRecommendedUserDto toHomeRecommendedUserDto(RecommendedUserDto dto) {
        return HomeRecommendedUserDto.builder()
                .userId(dto.getUserId())
                .name(dto.getName())
                .imageUrl(dto.getImageUrl())
                .bio(dto.getBio())
                .matchReason(dto.getMatchReason())
                .build();
    }
}
