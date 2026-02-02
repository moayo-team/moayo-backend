package com.moayo.moayobackend.chat.controller;

import com.moayo.moayobackend.chat.dto.response.ChatMessageResponse;
import com.moayo.moayobackend.chat.service.ChatMessageService;
import com.moayo.moayobackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
    name = "채팅 메시지",
    description = "채팅방 메시지 관련 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/rooms")
public class ChatMessageRestController {
    private final ChatMessageService chatMessageService;

    @Operation(
            summary = "채팅방 내 메세지 목록 조회",
            description = """
                지정된 채팅방(roomId)의 메시지를 생성일시 기준 오름차순으로 조회합니다.
                로그인한 사용자이면서 해당 채팅방 참여자인 경우에만 조회할 수 있습니다.
                """
    )
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessagesByRoom(
            @PathVariable("roomId") Long roomId,
            @AuthenticationPrincipal Long userId
    ){
        List<ChatMessageResponse> messages = chatMessageService.getMessagesByRoomId(roomId, userId);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "CHAT200_2",
                        "채팅방 메시지 조회에 성공했습니다.",
                        messages
                )
        );
    }
}