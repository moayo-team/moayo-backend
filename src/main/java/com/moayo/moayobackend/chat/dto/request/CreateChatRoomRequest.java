package com.moayo.moayobackend.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateChatRoomRequest {
    private Long userBId;
    private Long originPostId;
}
