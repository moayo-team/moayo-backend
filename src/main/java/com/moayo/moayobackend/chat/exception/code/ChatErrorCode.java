package com.moayo.moayobackend.chat.exception.code;

import com.moayo.moayobackend.global.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements BaseErrorCode {
    // Chat Setting
    // 400 BAD REQUEST
    CHAT_USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "CHAT_ROOM400_1", "채팅 참여자 ID가 필요합니다."),
    CHAT_CANNOT_CHAT_WITH_SELF(HttpStatus.BAD_REQUEST, "CHAT_ROOM400_2", "자기 자신과는 채팅을 생성할 수 없습니다."),
    CHAT_ORIGIN_POST_ID_REQUIRED(HttpStatus.BAD_REQUEST, "CHAT_ROOM400_3", "채팅 생성 시 기준 게시글 ID가 필요합니다."),

    // 403 FORBIDDEN
    CHAT_ROOM_FORBIDDEN(
            HttpStatus.FORBIDDEN, "CHAT_ROOM403_1", "이 채팅방에 접근 권한이 없습니다."
    ),

    // 404 NOT FOUND
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_ROOM404_1", "채팅방을 찾을 수 없습니다."),

    // 409 CONFLICT
    CHAT_ROOM_CREATE_CONFLICT(HttpStatus.CONFLICT, "CHAT_ROOM409_1", "채팅방 생성 중 충돌이 발생했습니다."),

    // Message
    // 400 BAD REQUEST
    MESSAGE_CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "MESSAGE400_2", "메시지 내용은 비어 있을 수 없습니다."),

    // 404 NOT FOUND
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE404_1", "해당 메시지를 찾을 수 없습니다."
    ),

    // 500 INTERNAL SERVER ERROR
    MESSAGE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MESSAGE500_1", "메시지 저장에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
