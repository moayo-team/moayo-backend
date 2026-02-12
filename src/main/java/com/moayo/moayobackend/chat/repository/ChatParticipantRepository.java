package com.moayo.moayobackend.chat.repository;

import com.moayo.moayobackend.chat.entity.ChatParticipant;
import com.moayo.moayobackend.chat.repository.projection.ChatRoomListItemProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);
	Optional<ChatParticipant> findFirstByChatRoomIdAndUserIdNot(Long chatRoomId, Long userId);
    @Query(value = """
	SELECT
	    cp.chat_room_id AS roomId,
	    other.user_id   AS opponentUserId,
	    pr.image_url    AS opponentImageUrl,
	    m.content       AS lastMessageContent,
	    m.created_at    AS lastMessageCreatedAt,
	    CASE
	        WHEN EXISTS (
	            SELECT 1
	            FROM message m3
	            WHERE m3.chat_room_id = cp.chat_room_id
	              AND m3.sender_id <> :userId
	              AND (
	                   cp.last_message_id IS NULL
	                   OR m3.id > cp.last_message_id
	              )
	        )
	        THEN TRUE
	        ELSE FALSE
	    END AS hasUnread
	FROM chat_participants cp
	JOIN chat_participants other
	    ON cp.chat_room_id = other.chat_room_id
	    AND other.user_id <> :userId
	LEFT JOIN profiles pr
	    ON pr.user_id = other.user_id
	LEFT JOIN message m
	    ON m.id = (
	        SELECT m2.id
	        FROM message m2
	        WHERE m2.chat_room_id = cp.chat_room_id
	        ORDER BY m2.created_at DESC
	        LIMIT 1
	    )
	WHERE cp.user_id = :userId
	ORDER BY m.created_at DESC, cp.chat_room_id DESC	
	""", nativeQuery = true)
    List<ChatRoomListItemProjection> findChatRoomListByUserId(
            @Param("userId") Long userId
    );

	@Modifying
	@Query("""
		UPDATE ChatParticipant cp
		SET cp.lastMessageId = :messageId
		WHERE cp.chatRoomId = :roomId
		  AND cp.userId = :userId
	""")
	int updateLastReadMessage(
			@Param("roomId") Long roomId,
			@Param("userId") Long userId,
			@Param("messageId") Long messageId
	);

	@Query(value = """
	SELECT
	    cp.chat_room_id AS roomId,
	    other.user_id   AS opponentUserId,
	    pr.image_url    AS opponentImageUrl,
	    m.content       AS lastMessageContent,
	    m.created_at    AS lastMessageCreatedAt,
	    CASE
	        WHEN EXISTS (
	            SELECT 1
	            FROM message m3
	            WHERE m3.chat_room_id = cp.chat_room_id
	              AND m3.sender_id <> :userId
	              AND (
	                   cp.last_message_id IS NULL
	                   OR m3.id > cp.last_message_id
	              )
	        )
	        THEN 1
	        ELSE 0
	    END AS hasUnread
	FROM chat_participants cp
	JOIN chat_participants other
	    ON cp.chat_room_id = other.chat_room_id
	    AND other.user_id <> :userId
	LEFT JOIN profiles pr
	    ON pr.user_id = other.user_id
	LEFT JOIN message m
	    ON m.id = (
	        SELECT m2.id
	        FROM message m2
	        WHERE m2.chat_room_id = cp.chat_room_id
	        ORDER BY m2.created_at DESC
	        LIMIT 1
	    )
	WHERE cp.user_id = :userId
	  AND cp.chat_room_id = :roomId
	LIMIT 1
	""", nativeQuery = true)
	Optional<ChatRoomListItemProjection> findChatRoomListItemByUserIdAndRoomId(
			@Param("userId") Long userId,
			@Param("roomId") Long roomId
	);


}
