package com.example.Roomy.chat.controller.privatechat;

import com.example.Roomy.chat.dto.ChatMessageDTO;
import com.example.Roomy.chat.dto.ChatRoomDTO;
import com.example.Roomy.chat.service.privatechat.PrivateChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/onetoonechat")
public class PrivateChatController {

    private final PrivateChatService privateChatService;

    public PrivateChatController(PrivateChatService privateChatService) {
        this.privateChatService = privateChatService;
    }

    // 사용자가 참여 중인 채팅방 조회
    @GetMapping("/rooms")
    public CompletableFuture<List<String>> getUserChatRooms(@RequestParam String userId) {
        return privateChatService.getUserChatRooms(userId);
    }

    // 채팅방 생성
    @PostMapping("/createchat")
    public ResponseEntity<String> createChatRoom(
            @RequestParam String userId1,
            @RequestParam String userId2) {
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setUsers(List.of(userId1, userId2));

        String roomId = privateChatService.createChatRoom(userId1, userId2, chatRoomDTO);
        return ResponseEntity.ok("Chat room created with ID: " + roomId);
    }

    // 메시지 조회 API
    @GetMapping("/room/{roomId}/messages")
    public CompletableFuture<ResponseEntity<List<ChatMessageDTO>>> getMessages(@PathVariable String roomId) {
        return privateChatService.getMessages(roomId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).build());
    }

    @DeleteMapping("/room/{roomId}/message/{messageId}")
    public CompletableFuture<ResponseEntity<String>> deleteMessage(
            @PathVariable String roomId,
            @PathVariable String messageId,
            @RequestParam String userId) {
        return privateChatService.deleteMessage(roomId, messageId, userId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof ResponseStatusException) {
                        ResponseStatusException rse = (ResponseStatusException) ex.getCause();
                        return ResponseEntity.status(rse.getStatusCode()).body(rse.getReason());
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류 발생");
                });
    }


    // 메시지 전송 API
    @PostMapping("/room/{roomId}/message")
    public CompletableFuture<ResponseEntity<String>> sendMessage(
            @PathVariable String roomId, @RequestBody ChatMessageDTO message) {
        return privateChatService.sendMessage(roomId, message)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof ResponseStatusException) {
                        ResponseStatusException rse = (ResponseStatusException) ex.getCause();
                        return ResponseEntity.status(rse.getStatusCode()).body(rse.getReason());
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error occurred");
                });
    }

    // 채팅방 나가기
    @DeleteMapping("/room/{roomId}")
    public void leaveRoom(@PathVariable String roomId, @RequestParam String userId) {
        privateChatService.leaveRoom(roomId, userId);
    }
}
