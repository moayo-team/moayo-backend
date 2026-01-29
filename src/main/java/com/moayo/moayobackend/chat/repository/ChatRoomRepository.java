package com.moayo.moayobackend.chat.repository;

import com.moayo.moayobackend.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // room_key를 통해 채팅방 찾기
    Optional<ChatRoom> findByRoomKey(String roomKey);
}
