package com.moayo.moayobackend.chat.dto.response;

import java.time.LocalDateTime;

public record ChatRoomListItemResponse(
        Long roomId,
        Long opponentUserId,
        String opponentImageUrl,
        String lastMessageContent,
        LocalDateTime lastMessageCreatedAt,
        boolean hasUnread
) {}
