package com.moayo.moayobackend.chat.dto.response;

import com.moayo.moayobackend.chat.entity.Message;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private String createdAt;

    public static ChatMessageResponse from(Message message) {
        String createdAtIso = message.getCreatedAt()
                .truncatedTo(ChronoUnit.MILLIS)                    // 2026-02-03T15:07:18.214
                .atZone(ZoneId.of("UTC"))                 // 서버 타임존 적용 (보통 UTC)
                .toInstant()                                       // UTC 기준 Instant
                .toString();                                       // 2026-02-03T06:07:18.214Z


        return ChatMessageResponse.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoomId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .createdAt(createdAtIso)
                .build();
    }
}