package com.moayo.moayobackend.home.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeRecommendedUserDto {
    private Long userId;
    private String name;
    private String imageUrl;
    private String bio;
    private String matchReason;
}
