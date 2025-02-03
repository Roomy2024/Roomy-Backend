package com.example.Roomy.chat.service.groupchat;

import com.example.Roomy.SocialLogin.UserRepository;
import com.example.Roomy.chat.dto.ChatMessageDTO;
import com.example.Roomy.chat.dto.ChatRoomDTO;
import com.example.Roomy.chat.util.TimeUtil;
import com.example.Roomy.SocialLogin.Entity.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GroupChatServiceImpl implements GroupChatService {

    private final DatabaseReference groupChatReference;
    private final UserRepository userRepository;

    public GroupChatServiceImpl(FirebaseApp firebaseApp, UserRepository userRepository) {
        this.groupChatReference = FirebaseDatabase.getInstance(firebaseApp).getReference("groupChats");
        this.userRepository = userRepository;
    }

    @Override
    public CompletableFuture<List<String>> getUserGroupChatRooms(String userId) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        groupChatReference.orderByChild("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> roomNames = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    List<String> users = getUsersFromSnapshot(child.child("users"));
                    if (users.contains(userId)) {
                        roomNames.add(child.getKey());
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

    @Override
    public CompletableFuture<String> sendGroupMessage(String roomId, ChatMessageDTO message) {
        CompletableFuture<String> future = new CompletableFuture<>();

        groupChatReference.child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // 방의 유저 목록 가져오기
                List<String> users = getUsersFromSnapshot(snapshot);

                if (users.isEmpty()) {
                    // 방에 유저가 전혀 없는 경우
                    future.completeExceptionally(new RuntimeException("Cannot send message: No users exist in the chat room: " + roomId));
                    return;
                }

                if (!users.contains(message.getSender())) {
                    // sender가 방의 유저가 아닌 경우
                    future.completeExceptionally(new RuntimeException("Sender is not part of the chat room: " + message.getSender()));
                    return;
                }

                // 메시지 전송
                String messageId = groupChatReference.child(roomId).child("messages").push().getKey();
                if (messageId != null) {
                    message.setMessageId(messageId);
                    message.setTimestamp(System.currentTimeMillis());
                    groupChatReference.child(roomId).child("messages").child(messageId).setValue(message, (error, ref) -> {
                        if (error != null) {
                            future.completeExceptionally(new RuntimeException("Failed to send group message: " + error.getMessage()));
                        } else {
                            future.complete("Message sent successfully");
                        }
                    });
                } else {
                    future.completeExceptionally(new RuntimeException("Failed to generate message ID"));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new RuntimeException("Failed to check users in chat room: " + error.getMessage()));
            }
        });

        return future;
    }

    @Override
    public String createGroupChatRoom(String groupName, List<String> userIds, ChatRoomDTO chatRoomDTO) {

        if (userIds == null){
            userIds = new ArrayList<>();
        }

        String roomId = groupName.replace(" ", "_") + "_room";
        chatRoomDTO.setRoomId(roomId);
        chatRoomDTO.setUsers(userIds);

        groupChatReference.child(roomId).setValueAsync(chatRoomDTO);
        return roomId;
    }

    @Override
    public void leaveGroupRoom(String roomId, String userId) {
        groupChatReference.child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> users = getUsersFromSnapshot(snapshot);
                if (users != null && users.contains(userId)) {
                    users.remove(userId);
                    if (users.isEmpty()) {
                        groupChatReference.child(roomId).removeValueAsync();
                    } else {
                        groupChatReference.child(roomId).child("users").setValueAsync(users);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                throw new RuntimeException("Failed to leave group room: " + error.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<String> deleteGroupMessage(String roomId, String messageId, String userId) {
        CompletableFuture<String> future = new CompletableFuture<>();

        groupChatReference.child(roomId).child("messages").child(messageId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            future.completeExceptionally(new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."));
                            return;
                        }

                        // 작성자 검증
                        String sender = snapshot.child("sender").getValue(String.class);
                        if (sender == null || !sender.equals(userId)) {
                            future.completeExceptionally(new ResponseStatusException(
                                    HttpStatus.FORBIDDEN, "작성자만 메시지를 삭제할 수 있습니다."));
                            return;
                        }

                        // 삭제된 메시지로 변경
                        snapshot.getRef().child("content").setValue("삭제된 메세지 입니다", (error, ref) -> {
                            if (error != null) {
                                future.completeExceptionally(new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR, "메시지 삭제 실패: " + error.getMessage()));
                            } else {
                                future.complete("메시지 내용이 '삭제된 메세지 입니다'로 업데이트되었습니다.");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        future.completeExceptionally(new RuntimeException(error.getMessage()));
                    }
                });

        return future;
    }


    @Override
    public CompletableFuture<List<ChatMessageDTO>> getGroupMessages(String roomId) {
        CompletableFuture<List<ChatMessageDTO>> future = new CompletableFuture<>();
        groupChatReference.child(roomId).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    public void addUserToGroupChat(String roomId, String userId) {
        groupChatReference.child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> users = getUsersFromSnapshot(snapshot);
                if (users == null) {
                    users = new ArrayList<>();
                }

                // 새로운 사용자가 이미 존재하는지 확인
                if (!users.contains(userId)) {
                    users.add(userId);

                    // Firebase에 업데이트
                    groupChatReference.child(roomId).child("users").setValue(users, (error, ref) -> {
                        if (error != null) {
                            System.err.println("Failed to add user: " + error.getMessage());
                        } else {
                            System.out.println("User added successfully");
                        }
                    });
                } else {
                    System.out.println("User already in the chat room");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Failed to fetch users: " + error.getMessage());
            }
        });
    }


    @Override
    public List<String> getUsersInGroupChat(String roomId) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        groupChatReference.child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> users = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String userId = child.getValue(String.class);
                    if (userId != null) {
                        users.add(userId);
                    }
                }
                future.complete(users);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new RuntimeException("Failed to fetch users: " + error.getMessage()));
            }
        });

        try {
            // CompletableFuture를 사용해 동기적으로 결과 반환
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch users in group chat", e);
        }
    }

    private List<String> getUsersFromSnapshot(DataSnapshot snapshot) {
        List<String> users = new ArrayList<>();
        if (snapshot.exists()) {
            for (DataSnapshot child : snapshot.getChildren()) {
                String userId = child.getValue(String.class);
                if (userId != null) {
                    users.add(userId);
                }
            }
        }
        return users;
    }
}
