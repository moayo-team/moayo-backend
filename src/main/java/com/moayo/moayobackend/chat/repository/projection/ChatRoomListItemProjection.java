package com.moayo.moayobackend.chat.repository.projection;

import java.time.LocalDateTime;

public interface ChatRoomListItemProjection {
    Long getRoomId();
    Long getOpponentUserId();
    String getOpponentImageUrl();
    String getLastMessageContent();
    LocalDateTime getLastMessageCreatedAt();
    Integer getHasUnread();
}
