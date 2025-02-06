package com.example.Roomy.chat.service.groupchat;

import com.example.Roomy.chat.dto.ChatMessageDTO;
import com.example.Roomy.chat.dto.ChatRoomDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GroupChatService {
    CompletableFuture<List<String>> getUserGroupChatRooms(String userId);
    CompletableFuture<String> sendGroupMessage(String roomId, ChatMessageDTO message);
    String createGroupChatRoom(String groupName, List<String> userIds, ChatRoomDTO chatRoomDTO);
    void leaveGroupRoom(String roomId, String userId);
    CompletableFuture<List<ChatMessageDTO>> getGroupMessages(String roomId);
    void addUserToGroupChat(String roomId, String userId);
    List<String> getUsersInGroupChat(String roomId);
    CompletableFuture<String> deleteGroupMessage(String roomId, String messageId, String userId);
}
