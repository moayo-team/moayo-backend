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
        },
        indexes = {
                @Index(
                        name = "idx_chat_rooms_origin_post_id",
                        columnList = "origin_post_id"
                )
        }
)
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 방이 처음 만들어질 때 기준이 된 게시글
    @Column(name = "origin_post_id")
    private Long originPostId;

    // 두 유저 조합(정렬된) 기준 고유 키
    @Column(name = "room_key", nullable = false, length = 100)
    private String roomKey;
}
