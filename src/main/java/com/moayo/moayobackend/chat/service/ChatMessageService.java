package com.moayo.moayobackend.chat.service;

import com.moayo.moayobackend.chat.dto.request.ChatMessageRequest;
import com.moayo.moayobackend.chat.dto.response.ChatMessageResponse;
import com.moayo.moayobackend.chat.entity.Message;
import com.moayo.moayobackend.chat.exception.ChatException;
import com.moayo.moayobackend.chat.exception.code.ChatErrorCode;
import com.moayo.moayobackend.chat.repository.ChatParticipantRepository;
import com.moayo.moayobackend.chat.repository.ChatRoomRepository;
import com.moayo.moayobackend.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public ChatMessageResponse sendMessage(Long chatRoomId, Long userId, ChatMessageRequest request) {
        // 내용이 비었는지 체크
        if (!StringUtils.hasText(request.getContent())){
            throw new ChatException(ChatErrorCode.MESSAGE_CONTENT_EMPTY);
        }

        // 방 존재 여부 체크
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        // 참가자인지 체크
        boolean isParticipant =
                chatParticipantRepository.existsByChatRoomIdAndUserId(chatRoomId, userId);

        if (!isParticipant) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_FORBIDDEN);
        }

        // 메세지 생성
        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .senderId(userId)
                .content(request.getContent())
                .build();
        // 메세지 저장 (자동)
        Message saved = messageRepository.save(message);

        // 응답 DTO로 변환
        return ChatMessageResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessagesByRoomId(Long chatRoomId, Long userId){
        if (!chatRoomRepository.existsById(chatRoomId)){
            throw new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        boolean isParticipant =
                chatParticipantRepository.existsByChatRoomIdAndUserId(chatRoomId, userId);

        if (!isParticipant) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_FORBIDDEN);
        }

        return messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId)
                .stream()
                .map(ChatMessageResponse::from)
                .toList();
    }
}