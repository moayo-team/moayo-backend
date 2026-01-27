package com.moayo.moayobackend.chat.repository;

import com.moayo.moayobackend.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    @Query("""
		SELECT m.id
		FROM Message m
		WHERE m.chatRoomId = :roomId
		ORDER BY m.createdAt DESC
	""")
    List<Long> findLastMessageIdsByRoomId(@Param("roomId") Long roomId);
}
