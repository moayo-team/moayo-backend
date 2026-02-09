package com.moayo.moayobackend.chat.entity;

import com.moayo.moayobackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table(
        name = "chat_rooms",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chat_rooms_room_key",
                        columnNames = {"room_key"}
                )
        }
)
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 두 유저 조합(정렬된) 기준 고유 키
    @Column(name = "room_key", nullable = false, length = 100)
    private String roomKey;
}
