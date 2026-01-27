package com.moayo.moayobackend.chat.controller;

import com.moayo.moayobackend.chat.dto.request.ChatMessageRequest;
import com.moayo.moayobackend.chat.dto.response.ChatMessageResponse;
import com.moayo.moayobackend.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageStompController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트에서 SEND /app/chat/rooms/{roomId}
    @MessageMapping("/chat/rooms/{roomId}")
    public void sendToRoom(@DestinationVariable Long roomId, ChatMessageRequest payload, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        // 1) DB에 메시지 저장 (방 존재/참가자 검증 포함)
        ChatMessageResponse response = chatMessageService.sendMessage(roomId, userId, payload);

        // 2) 해당 방 구독자에게 메시지 전송
        String destination = "/topic/chat/rooms/" + roomId;
        messagingTemplate.convertAndSend(destination, response);
    }
}
