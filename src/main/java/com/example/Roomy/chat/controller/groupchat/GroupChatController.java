package com.example.Roomy.chat.controller.groupchat;

import com.example.Roomy.chat.dto.ChatMessageDTO;
import com.example.Roomy.chat.dto.ChatRoomDTO;
import com.example.Roomy.chat.service.groupchat.GroupChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/groupchat")
public class GroupChatController {

    private final GroupChatService groupChatService;

    public GroupChatController(GroupChatService groupChatService){
        this.groupChatService = groupChatService;
    }

    @GetMapping("/rooms")
    public CompletableFuture<List<String>> getUserGroupChatRooms(@RequestParam String userId){
        return groupChatService.getUserGroupChatRooms(userId);
    }

    @PostMapping("/createchat")
    public ResponseEntity<String> createGroupChatRoom(
            @RequestParam String groupName, // 쿼리 파라미터로 groupName 받기
            @RequestBody(required = false) Map<String, List<String>> requestBody // userIds가 없어도 허용
    ) {
        // userIds가 요청 Body에 없으면 빈 리스트로 초기화
        List<String> userIds = (requestBody != null && requestBody.containsKey("userIds"))
                ? requestBody.get("userIds")
                : new ArrayList<>();

        // groupName 유효성 검사
        if (groupName == null || groupName.isEmpty()) {
            return ResponseEntity.badRequest().body("Group name is required");
        }

        // ChatRoomDTO 생성 및 데이터 설정
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setRoomId(groupName.replace(" ", "_") + "_room");
        chatRoomDTO.setUsers(userIds);

        // 그룹 채팅방 생성
        String roomId = groupChatService.createGroupChatRoom(groupName, userIds, chatRoomDTO);

        return ResponseEntity.ok("Group chat room created with ID: " + roomId);
    }

    @DeleteMapping("/room/{roomId}/message/{messageId}")
    public CompletableFuture<ResponseEntity<String>> deleteGroupMessage(
            @PathVariable String roomId,
            @PathVariable String messageId,
            @RequestParam String userId) {
        return groupChatService.deleteGroupMessage(roomId, messageId, userId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof ResponseStatusException) {
                        ResponseStatusException rse = (ResponseStatusException) ex.getCause();
                        return ResponseEntity.status(rse.getStatusCode()).body(rse.getReason());
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류 발생");
                });
    }



    @PostMapping("/room/{roomId}/message")
    public CompletableFuture<ResponseEntity<String>> sendGroupMessage(@PathVariable String roomId, @RequestBody ChatMessageDTO message){
        return groupChatService.sendGroupMessage(roomId, message)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/room/{roomId}/messages")
    public CompletableFuture<ResponseEntity<List<ChatMessageDTO>>> getGroupMessages(@PathVariable String roomId){
        return groupChatService.getGroupMessages(roomId)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/room/{roomId}")
    public void leaveGroupRoom(@PathVariable String roomId, @RequestParam String userId){
        groupChatService.leaveGroupRoom(roomId, userId);
    }

    // 그룹 채팅방에 사용자 추가
    @PostMapping("/room/{roomId}/addUser")
    public ResponseEntity<String> addUserToGroupChat(@PathVariable String roomId, @RequestParam String userId) {
        try {
            groupChatService.addUserToGroupChat(roomId, userId);
            return ResponseEntity.ok("User added to group chat successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add user: " + e.getMessage());
        }
    }

    @GetMapping("/room/{roomId}/users")
    public ResponseEntity<List<String>> getUsersInGroupChat(@PathVariable String roomId) {
        try {
            List<String> users = groupChatService.getUsersInGroupChat(roomId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
