package com.moayo.moayobackend.chat.service;

import com.moayo.moayobackend.chat.entity.ChatParticipant;
import com.moayo.moayobackend.chat.entity.ChatRoom;
import com.moayo.moayobackend.chat.exception.ChatException;
import com.moayo.moayobackend.chat.exception.code.ChatErrorCode;
import com.moayo.moayobackend.chat.repository.ChatParticipantRepository;
import com.moayo.moayobackend.chat.repository.ChatRoomRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    @Transactional // user A와 user B 사이의 채팅방 id 반환 (없으면 방을 생성해 반환)
    public Long getOrCreateRoom(Long userAId, Long userBId, Long originPostId) {

        if (userAId == null || userBId == null) { // 둘의 id 검증
            throw new ChatException(ChatErrorCode.CHAT_USER_ID_REQUIRED);
        }

        if (userAId.equals(userBId)) { // 자기 자신과는 채팅방 생성 불가
            throw new ChatException(ChatErrorCode.CHAT_CANNOT_CHAT_WITH_SELF);
        }

        if (originPostId == null) { // 채팅을 시작한 게시글
            throw new ChatException(ChatErrorCode.CHAT_ORIGIN_POST_ID_REQUIRED);
        }
        // user A와 user B에 대한 채팅방이 있는지 검증 (room_key 이용)
        Long small = Math.min(userAId, userBId);
        Long big = Math.max(userAId, userBId);
        String roomKey = small + ":" + big;

        return chatRoomRepository.findByRoomKey(roomKey)
                .map(ChatRoom::getId)
                .orElseGet(() -> createRoom(roomKey, originPostId, userAId, userBId));
    }

    // 채팅방 생성
    private Long createRoom(
            String roomKey,
            Long originPostId,
            Long userAId,
            Long userBId
    ) {
        try { // 참여자 등록
            ChatRoom room = chatRoomRepository.save(
                    ChatRoom.builder()
                            .roomKey(roomKey)
                            .originPostId(originPostId)
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
}
