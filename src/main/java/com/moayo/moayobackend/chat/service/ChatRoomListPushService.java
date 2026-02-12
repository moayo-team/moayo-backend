package com.moayo.moayobackend.chat.service;

import com.moayo.moayobackend.chat.dto.response.ChatRoomListItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
public class ChatRoomListPushService {
    private final SimpMessagingTemplate messagingTemplate; // subscriber 에게 message push

    // 각 user별 message list update
    public void push(Long userId, ChatRoomListItemResponse payload){
        messagingTemplate.convertAndSend(
                "/topic/chat/threads/" + userId,
                payload
        );
    }
}
