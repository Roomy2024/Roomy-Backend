package com.example.Roomy.chat.service.privatechat;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.chat.dto.ChatMessageDTO;
import com.example.Roomy.chat.dto.ChatRoomDTO;
import com.example.Roomy.chat.util.TimeUtil;
import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PrivateChatServiceImpl implements PrivateChatService {

    private final DatabaseReference databaseReference;
    private final UserRepository userRepository;

    public PrivateChatServiceImpl(FirebaseApp firebaseApp, UserRepository userRepository) {
        this.databaseReference = FirebaseDatabase.getInstance(firebaseApp).getReference("privateChats");
        this.userRepository = userRepository;
    }

    //유저가 가지고 있는 채팅방 보기
    @Override
    public CompletableFuture<List<String>> getUserChatRooms(String userId) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        databaseReference.orderByChild("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> roomNames = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    List<String> users = getUsersFromSnapshot(child.child("users"));
                    if (users.contains(userId)) {
                        users.remove(userId);
                        if (!users.isEmpty()) {
                            String otherUserId = users.get(0);
                            User otherUser = userRepository.findById(Long.valueOf(otherUserId)).orElse(null);
                            if (otherUser != null) {
                                String roomName = otherUser.getUsername() + " 님과의 채팅방";
                                roomNames.add(roomName);
                            } else {
                                roomNames.add("알 수 없는 사용자 님과의 채팅방");
                            }
                        }
                    }
                }
                future.complete(roomNames);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            }
        });
        return future;
    }

    //메세지 보내기
    @Override
    public CompletableFuture<String> sendMessage(String roomId, ChatMessageDTO message) {
        CompletableFuture<String> future = new CompletableFuture<>();
        databaseReference.child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> users = getUsersFromSnapshot(snapshot);
                if (!users.contains(message.getSender())) {
                    future.completeExceptionally(new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "User " + message.getSender() + " is not part of the chat room."));
                    return;
                }
                String messageId = databaseReference.child(roomId).child("messages").push().getKey();
                if (messageId != null) {
                    message.setTimestamp(System.currentTimeMillis());
                    message.setMessageId(messageId);
                    databaseReference.child(roomId).child("messages").child(messageId)
                            .setValue(message, (error, ref) -> {
                                if (error != null) {
                                    future.completeExceptionally(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                            "Failed to send message: " + error.getMessage()));
                                } else {
                                    future.complete("Message sent successfully");
                                }
                            });
                } else {
                    future.completeExceptionally(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to generate message ID"));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            }
        });
        return future;
    }

    //방만들기
    @Override
    public String createChatRoom(String userId1, String userId2, ChatRoomDTO chatRoomDTO) {
        User user1 = userRepository.findById(Long.valueOf(userId1)).orElse(null);
        User user2 = userRepository.findById(Long.valueOf(userId2)).orElse(null);

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("One or both users not found");
        }

        String roomId1 = user1.getUsername() + "_" + user2.getUsername() + "_room";
        String roomId2 = user2.getUsername() + "_" + user1.getUsername() + "_room";

        CompletableFuture<String> existingRoomCheck = checkIfRoomExists(roomId1, roomId2);
        try {
            String existingRoomId = existingRoomCheck.get();
            if (existingRoomId != null) {
                return existingRoomId;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to check existing chat room", e);
        }

        chatRoomDTO.setRoomId(roomId1);
        ApiFuture<Void> future = databaseReference.child(roomId1).setValueAsync(chatRoomDTO);

        try {
            future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create chat room", e);
        }

        return roomId1;
    }

    // 방 나가기
    @Override
    public void leaveRoom(String roomId, String userId) {
        // Firebase에서 해당 채팅방의 사용자 목록을 가져옴
        databaseReference.child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> users = getUsersFromSnapshot(snapshot);
                if (users != null && users.contains(userId)) {
                    users.remove(userId);

                    // 나간 사용자의 정보 가져오기
                    User user = userRepository.findById(Long.valueOf(userId)).orElse(null);
                    if (user != null) {
                        // 시스템 메시지 추가
                        String messageId = databaseReference.child(roomId).child("messages").push().getKey();
                        if (messageId != null) {
                            ChatMessageDTO systemMessage = new ChatMessageDTO();
                            systemMessage.setMessageId(messageId);
                            systemMessage.setSender("system");
                            systemMessage.setContent(user.getUsername() + "님이 채팅방에서 나갔습니다.");
                            systemMessage.setTimestamp(System.currentTimeMillis());

                            databaseReference.child(roomId).child("messages").child(messageId).setValue(systemMessage, (error, ref) -> {
                                if (error != null) {
                                    System.err.println("Failed to add system message: " + error.getMessage());
                                }
                            });
                        }
                    }

                    // 사용자가 모두 나갔으면 방 삭제, 아니면 사용자 목록 업데이트
                    if (users.isEmpty()) {
                        databaseReference.child(roomId).removeValueAsync();
                    } else {
                        databaseReference.child(roomId).child("users").setValueAsync(users);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                throw new RuntimeException("Failed to leave room: " + error.getMessage());
            }
        });
    }


    // 메시지 보기
    @Override
    public CompletableFuture<List<ChatMessageDTO>> getMessages(String roomId) {
        CompletableFuture<List<ChatMessageDTO>> future = new CompletableFuture<>();
        databaseReference.child(roomId).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<ChatMessageDTO> messages = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ChatMessageDTO message = child.getValue(ChatMessageDTO.class);
                    if (message != null) {
                        String formattedTime = TimeUtil.convertTimestampToKoreanTime(message.getTimestamp());
                        message.setFormattedTimestamp(formattedTime);
                        messages.add(message);
                    }
                }
                future.complete(messages);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            }
        });
        return future;
    }

    private List<String> getUsersFromSnapshot(DataSnapshot snapshot) {
        List<String> users = new ArrayList<>();
        for (DataSnapshot child : snapshot.getChildren()) {
            String user = child.getValue(String.class);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    //중복 방 확인
    private CompletableFuture<String> checkIfRoomExists(String roomId1, String roomId2) {
        CompletableFuture<String> future = new CompletableFuture<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(roomId1)) {
                    future.complete(roomId1);
                } else if (snapshot.hasChild(roomId2)) {
                    future.complete(roomId2);
                } else {
                    future.complete(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            }
        });
        return future;
    }
}
