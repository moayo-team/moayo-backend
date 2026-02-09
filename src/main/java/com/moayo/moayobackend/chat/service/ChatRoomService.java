package com.moayo.moayobackend.chat.service;

import com.moayo.moayobackend.chat.dto.response.ChatRoomListItemResponse;
import com.moayo.moayobackend.chat.entity.ChatParticipant;
import com.moayo.moayobackend.chat.entity.ChatRoom;
import com.moayo.moayobackend.chat.exception.ChatException;
import com.moayo.moayobackend.chat.exception.code.ChatErrorCode;
import com.moayo.moayobackend.chat.repository.ChatParticipantRepository;
import com.moayo.moayobackend.chat.repository.ChatRoomRepository;
import com.moayo.moayobackend.chat.repository.MessageRepository;
import com.moayo.moayobackend.chat.repository.projection.ChatRoomListItemProjection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MessageRepository messageRepository;

    @Transactional // user A와 user B 사이의 채팅방 id 반환 (없으면 방을 생성해 반환)
    public Long getOrCreateRoom(Long userAId, Long userBId) {

        if (userAId == null || userBId == null) { // 둘의 id 검증
            throw new ChatException(ChatErrorCode.CHAT_USER_ID_REQUIRED);
        }

        if (userAId.equals(userBId)) { // 자기 자신과는 채팅방 생성 불가
            throw new ChatException(ChatErrorCode.CHAT_CANNOT_CHAT_WITH_SELF);
        }

        // user A와 user B에 대한 채팅방이 있는지 검증 (room_key 이용)
        Long small = Math.min(userAId, userBId);
        Long big = Math.max(userAId, userBId);
        String roomKey = small + ":" + big;

        return chatRoomRepository.findByRoomKey(roomKey)
                .map(ChatRoom::getId)
                .orElseGet(() -> createRoom(roomKey, userAId, userBId));
    }

    // 채팅방 생성
    private Long createRoom(
            String roomKey,
            Long userAId,
            Long userBId
    ) {
        try { // 참여자 등록
            ChatRoom room = chatRoomRepository.save(
                    ChatRoom.builder()
                            .roomKey(roomKey)
                            .build()
            );

            chatParticipantRepository.save(
                    ChatParticipant.builder()
                            .chatRoomId(room.getId())
                            .userId(userAId)
                            .build()
            );

            chatParticipantRepository.save(
                    ChatParticipant.builder()
                            .chatRoomId(room.getId())
                            .userId(userBId)
                            .build()
            );

            return room.getId();

        } catch (DataIntegrityViolationException e) { // 동시에 채팅방 생성 요청 시, 채팅방 생성 충돌 409 에러 발생
            throw new ChatException(
                    ChatErrorCode.CHAT_ROOM_CREATE_CONFLICT
            );
        }
    }

    @Transactional(readOnly = true)
    public List<ChatRoomListItemResponse> getMyChatRooms(Long userId) {
        if (userId == null) {
            throw new ChatException(ChatErrorCode.CHAT_USER_ID_REQUIRED);
        }

        List<ChatRoomListItemProjection> rows =
                chatParticipantRepository.findChatRoomListByUserId(userId);

        return rows.stream()
                .map(row -> {
                    Integer unreadRaw = row.getHasUnread(); // 0 또는 1 또는 null
                    boolean hasUnread = (unreadRaw != null && unreadRaw == 1);

                    return new ChatRoomListItemResponse(
                            row.getRoomId(),
                            row.getOpponentUserId(),
                            row.getOpponentImageUrl(),
                            row.getLastMessageContent(),
                            row.getLastMessageCreatedAt(),
                            hasUnread
                    );
                })
                .toList();
    }

    @Transactional
    public void readChatRoom(Long roomId, Long userId) {
        if (userId == null) {
            throw new ChatException(ChatErrorCode.CHAT_USER_ID_REQUIRED);
        }

        // 이 유저가 이 방 참가자인지
        boolean isParticipant = chatParticipantRepository.existsByChatRoomIdAndUserId(roomId, userId);
        if (!isParticipant) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_FORBIDDEN);
        }

        // 이 방의 마지막 메시지 ID 조회
        var ids = messageRepository.findLastMessageIdsByRoomId(roomId);

        // 메시지가 하나도 없으면 읽음 처리할 게 없으니 그냥 리턴
        if (ids.isEmpty()) {
            return;
        }

        Long lastMessageId = ids.get(0);

        // 3. 내 참가자 row의 lastMessageId 업데이트
        chatParticipantRepository.updateLastReadMessage(
                roomId,
                userId,
                lastMessageId
        );
    }
}
