package com.moayo.moayobackend.user.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendationResponseDto {

    private String type; // similar | synergy
    private List<RecommendedUserDto> items;
}
