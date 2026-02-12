package com.moayo.moayobackend.chat.service;

import com.moayo.moayobackend.chat.dto.request.ChatMessageRequest;
import com.moayo.moayobackend.chat.dto.response.ChatMessageResponse;
import com.moayo.moayobackend.chat.dto.response.ChatRoomListItemResponse;
import com.moayo.moayobackend.chat.entity.Message;
import com.moayo.moayobackend.chat.exception.ChatException;
import com.moayo.moayobackend.chat.exception.code.ChatErrorCode;
import com.moayo.moayobackend.chat.repository.ChatParticipantRepository;
import com.moayo.moayobackend.chat.repository.ChatRoomRepository;
import com.moayo.moayobackend.chat.repository.MessageRepository;
import com.moayo.moayobackend.chat.repository.projection.ChatRoomListItemProjection;
import com.moayo.moayobackend.user.repository.UserRepository;
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

    private final ChatRoomListPushService chatRoomListPushService;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageResponse sendMessage(Long chatRoomId, Long userId, ChatMessageRequest request) {

        if (!StringUtils.hasText(request.getContent())){
            throw new ChatException(ChatErrorCode.MESSAGE_CONTENT_EMPTY);
        }

        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        boolean isParticipant =
                chatParticipantRepository.existsByChatRoomIdAndUserId(chatRoomId, userId);

        if (!isParticipant) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_FORBIDDEN);
        }

        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .senderId(userId)
                .content(request.getContent())
                .build();

        Message saved = messageRepository.save(message);

        // 상대 찾기
        Long opponentId = chatParticipantRepository
                .findFirstByChatRoomIdAndUserIdNot(chatRoomId, userId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND))
                .getUserId();

        // sender 기준 풀 DTO 조회
        ChatRoomListItemProjection myRow =
                chatParticipantRepository
                        .findChatRoomListItemByUserIdAndRoomId(userId, chatRoomId)
                        .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        chatRoomListPushService.push(
                userId,
                new ChatRoomListItemResponse(
                        myRow.getRoomId(),
                        myRow.getOpponentUserId(),
                        myRow.getOpponentImageUrl(),
                        myRow.getLastMessageContent(),
                        myRow.getLastMessageCreatedAt(),
                        false
                )
        );

        // opponent 기준 풀 DTO 조회
        ChatRoomListItemProjection opponentRow =
                chatParticipantRepository
                        .findChatRoomListItemByUserIdAndRoomId(opponentId, chatRoomId)
                        .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        chatRoomListPushService.push(
                opponentId,
                new ChatRoomListItemResponse(
                        opponentRow.getRoomId(),
                        opponentRow.getOpponentUserId(),
                        opponentRow.getOpponentImageUrl(),
                        opponentRow.getLastMessageContent(),
                        opponentRow.getLastMessageCreatedAt(),
                        true
                )
        );

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