package com.example.Roomy.chat.controller.privatechat;

import com.example.Roomy.chat.dto.ChatMessageDTO;
import com.example.Roomy.chat.dto.ChatRoomDTO;
import com.example.Roomy.chat.service.privatechat.PrivateChatServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/onetoonechat")
public class PrivateChatController {

    private final PrivateChatServiceImpl privateChatServiceImpl;

    public PrivateChatController(PrivateChatServiceImpl privateChatServiceImpl) {
        this.privateChatServiceImpl = privateChatServiceImpl;
    }

    // 사용자가 참여 중인 채팅방 조회
//    @GetMapping("/rooms")
//    public CompletableFuture<List<ChatRoomDTO>> getUserChatRooms(@RequestParam String userId) {
//        return privateChatService.getUserChatRooms(userId);
//    }
    @GetMapping("/rooms")
    public CompletableFuture<List<String>> getUserChatRooms(@RequestParam String userId) {
        return privateChatServiceImpl.getUserChatRooms(userId);
    }


//    //채팅방 생성
//    @PostMapping("/createchat")
//    public ResponseEntity<String> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
//        String roomId = privateChatService.createChatRoom(chatRoomDTO);
//        return ResponseEntity.ok("Chat room created with ID: " + roomId);
//    }

    @PostMapping("/createchat")
    public ResponseEntity<String> createChatRoom(
            @RequestParam String userId1,
            @RequestParam String userId2) {
        // ChatRoomDTO를 생성하고 필요한 정보 설정
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setUsers(List.of(userId1, userId2)); // 유저 ID 리스트 설정

        // 채팅방 생성
        String roomId = privateChatServiceImpl.createChatRoom(userId1, userId2, chatRoomDTO);
        return ResponseEntity.ok("Chat room created with ID: " + roomId);
    }


    // 메시지 조회 API
    @GetMapping("/room/{roomId}/messages")
    public CompletableFuture<ResponseEntity<List<ChatMessageDTO>>> getMessages(@PathVariable String roomId) {
        return privateChatServiceImpl.getMessages(roomId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(500).build());
    }

//    // 메시지 전송 API
//    @PostMapping("/room/{roomId}/message")
//    public ResponseEntity<String> sendMessage(@PathVariable String roomId, @RequestBody ChatMessageDTO message) {
//        if (message.getSender() == null || message.getContent() == null) {
//            return ResponseEntity.badRequest().body("Invalid message data");
//        }
//        privateChatService.sendMessage(roomId, message);
//        return ResponseEntity.ok("Message sent successfully");
//    }
    @PostMapping("/room/{roomId}/message")
    public CompletableFuture<ResponseEntity<String>> sendMessage(
            @PathVariable String roomId, @RequestBody ChatMessageDTO message) {
        return privateChatServiceImpl.sendMessage(roomId, message)
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
        privateChatServiceImpl.leaveRoom(roomId, userId);
    }
}
