package com.moayo.moayobackend.chat.controller;

import com.moayo.moayobackend.chat.dto.request.CreateChatRoomRequest;
import com.moayo.moayobackend.chat.dto.response.ChatRoomListItemResponse;
import com.moayo.moayobackend.chat.dto.response.CreateChatRoomResponse;
import com.moayo.moayobackend.chat.service.ChatRoomService;
import com.moayo.moayobackend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "채팅방", description = "채팅방 생성 및 조회 관련 API")
@RestController
@RequestMapping("/api/v1/chat")
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
                request.getUserBId()
        );

        return ResponseEntity.ok(
                ApiResponse.ok( "CHAT200_1",
                        "채팅방 생성 또는 조회에 성공했습니다.",
                        new CreateChatRoomResponse(roomId))
        );
    }

    @Operation(summary = "내 채팅방 목록 조회", description = "JWT 인증된 사용자 기준으로, 내가 참여중인 채팅방 목록을 최신순으로 조회합니다.")
    @GetMapping("/rooms")
    public ApiResponse<List<ChatRoomListItemResponse>> getMyChatRooms(
            @AuthenticationPrincipal Long userId
    ) {
        List<ChatRoomListItemResponse> result = chatRoomService.getMyChatRooms(userId);
        return ApiResponse.ok(
                "CHAT200_2",
                "채팅방 목록 조회에 성공했습니다.",
                result
        );
    }

    @Operation(summary = "채팅방 읽음 처리", description = "해당 채팅방의 마지막 메시지까지 읽음 처리합니다.")
    @PatchMapping("/rooms/{roomId}/read")
    public ApiResponse<Void> readChatRoom(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long roomId
    ) {
        chatRoomService.readChatRoom(roomId, userId);
        return ApiResponse.ok(
                "CHAT200_3",
                "채팅방 읽음 처리에 성공했습니다.",
                null
        );
    }
}