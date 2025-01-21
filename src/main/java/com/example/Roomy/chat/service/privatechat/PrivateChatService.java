package com.example.Roomy.chat.service.privatechat;

import com.example.Roomy.chat.dto.ChatMessageDTO;
import com.example.Roomy.chat.dto.ChatRoomDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PrivateChatService {
    CompletableFuture<List<String>> getUserChatRooms(String userId);
    CompletableFuture<String> sendMessage(String roomId, ChatMessageDTO message);
    String createChatRoom(String userId1, String userId2, ChatRoomDTO chatRoomDTO);
    void leaveRoom(String roomId, String userId);
    CompletableFuture<List<ChatMessageDTO>> getMessages(String roomId);
}
