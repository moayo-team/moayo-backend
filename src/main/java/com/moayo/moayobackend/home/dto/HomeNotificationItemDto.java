package com.moayo.moayobackend.home.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeNotificationItemDto {

    private Long notificationId;
    private String type;
    private String title;
    private String content;
    private boolean isRead;
    private String createdAt;
    private String targetType;
    private Long targetId;
}
