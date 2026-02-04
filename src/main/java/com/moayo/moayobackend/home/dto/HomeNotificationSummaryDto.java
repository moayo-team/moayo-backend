package com.moayo.moayobackend.home.dto;

import lombok.*;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeNotificationSummaryDto {
    private long unreadCount;
    private List<Object> items;
}