package com.moayo.moayobackend.user.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedUserDto {

    private Long userId;
    private String name;

    private String imageUrl;
    private String bio;
    private String matchReason;
}
