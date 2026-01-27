package com.moayo.moayobackend.chat.controller;

import com.moayo.moayobackend.chat.dto.request.CreateChatRoomRequest;
import com.moayo.moayobackend.chat.dto.response.CreateChatRoomResponse;
import com.moayo.moayobackend.chat.service.ChatRoomService;
import com.moayo.moayobackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "채팅방", description = "채팅방 생성 및 조회 관련 API")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방 생성(또는 조회)", description = "두 사용자의 조합을 기준으로 채팅방을 생성하거나, 이미 존재하는 경우 기존 채팅방을 반환합니다.")
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<CreateChatRoomResponse>> createOrGetRoom(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreateChatRoomRequest request
    ) {
        Long roomId = chatRoomService.getOrCreateRoom(
                userId,
                request.getUserBId(),
                request.getOriginPostId()
        );

        return ResponseEntity.ok(
                ApiResponse.ok( "CHAT200_1",
                        "채팅방 생성 또는 조회에 성공했습니다.",
                        new CreateChatRoomResponse(roomId))
        );
    }
}