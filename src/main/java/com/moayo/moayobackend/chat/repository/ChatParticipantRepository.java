package com.moayo.moayobackend.chat.repository;

import com.moayo.moayobackend.chat.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}
