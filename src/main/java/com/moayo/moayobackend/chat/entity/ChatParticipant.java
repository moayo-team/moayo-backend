package com.moayo.moayobackend.chat.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity // JPA의 엔티티임 명시
@Builder // @Bulider.Default 이용 시 필요
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 인자 없는 기본 생성자 자동 생성
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 모든 필드를 파라미터로 받는 생성자 자동 생성
@Getter // 모든 필드의 Getter 메서드 자동 생성
@Table(
        name = "chat_participants",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chat_room_user",
                        columnNames = {"chat_room_id", "user_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_chat_participants_user_id",
                        columnList = "user_id"
                )
        }
)
public class ChatParticipant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "last_message_id")
    private Long lastMessageId;
}
