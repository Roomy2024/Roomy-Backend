package com.example.Roomy.chat.service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.chat.dto.ChatMessageDTO;
import com.example.Roomy.chat.dto.ChatRoomDTO;
import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class PrivateChatService {

    private final DatabaseReference databaseReference;
    private final UserRepository userRepository;

    public PrivateChatService(FirebaseApp firebaseApp, UserRepository userRepository) {
        this.databaseReference = FirebaseDatabase.getInstance(firebaseApp).getReference("privateChats");
        this.userRepository = userRepository;
    }

//    // 사용자가 참여 중인 채팅방 조회
//    public CompletableFuture<List<ChatRoomDTO>> getUserChatRooms(String userId) {
//        CompletableFuture<List<ChatRoomDTO>> future = new CompletableFuture<>();
//        databaseReference.orderByChild("users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                List<ChatRoomDTO> rooms = new ArrayList<>();
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    List<String> users = getUsersFromSnapshot(child.child("users"));
//                    if (users.contains(userId)) {
//                        ChatRoomDTO room = new ChatRoomDTO();
//                        room.setRoomId(child.getKey());
//                        room.setUsers(users);
//                        rooms.add(room);
//                    }
//                }
//                future.complete(rooms);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                future.completeExceptionally(new RuntimeException(error.getMessage()));
//            }
//        });
//        return future;
//    }
// 사용자가 참여 중인 채팅방 조회
public CompletableFuture<List<String>> getUserChatRooms(String userId) {
    CompletableFuture<List<String>> future = new CompletableFuture<>();
    databaseReference.orderByChild("users").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            List<String> roomNames = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                List<String> users = getUsersFromSnapshot(child.child("users"));
                if (users.contains(userId)) {
                    // 조회한 userId를 제외한 나머지 유저 ID 찾기
                    users.remove(userId);
                    if (!users.isEmpty()) {
                        String otherUserId = users.get(0); // 상대방 유저 ID
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

    // 메시지 전송 메서드
    public CompletableFuture<String> sendMessage(String roomId, ChatMessageDTO message) {
        CompletableFuture<String> future = new CompletableFuture<>();

        // Firebase에서 채팅방 사용자 목록 가져오기
        databaseReference.child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> users = getUsersFromSnapshot(snapshot);

                // 메시지의 sender가 채팅방 사용자 목록에 포함되어 있는지 확인
                if (!users.contains(message.getSender())) {
                    future.completeExceptionally(new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "User " + message.getSender() + " is not part of the chat room."));
                    return;
                }

                // 메시지 ID 생성 및 설정
                String messageId = databaseReference.child(roomId).child("messages").push().getKey();
                if (messageId != null) {
                    message.setTimestamp(System.currentTimeMillis()); // 메시지 타임스탬프 설정
                    message.setMessageId(messageId); // 메시지 ID 설정
                    databaseReference.child(roomId).child("messages").child(messageId)
                            .setValue(message, (error, ref) -> {
                                if (error != null) {
                                    future.completeExceptionally(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                            "Failed to send message: " + error.getMessage()));
                                } else {
                                    System.out.println("Message sent successfully!");
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
                future.completeExceptionally(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to fetch chat room users: " + error.getMessage()));
            }
        });

        return future;
    }


// 채팅방 생성 메서드
//public String createChatRoom(String userId1, String userId2, ChatRoomDTO chatRoomDTO) {
//    // 유저 엔티티에서 username 가져오기
//    User user1 = userRepository.findById(Long.valueOf(userId1)).orElse(null);
//    User user2 = userRepository.findById(Long.valueOf(userId2)).orElse(null);
//
//    if (user1 == null || user2 == null) {
//        throw new IllegalArgumentException("One or both users not found");
//    }
//
//    // username 기반 roomId 생성
//    String roomId = user1.getUsername() + "_" + user2.getUsername() + "_room";
//
//    // ChatRoomDTO에 roomId 설정
//    chatRoomDTO.setRoomId(roomId);
//
//    // Firebase에 저장
//    ApiFuture<Void> future = databaseReference.child(roomId).setValueAsync(chatRoomDTO);
//
//    try {
//        // 작업 결과 확인
//        future.get(); // 작업이 완료될 때까지 대기
//        System.out.println("Chat room created successfully with ID: " + roomId);
//    } catch (Exception e) {
//        System.err.println("Failed to create chat room: " + e.getMessage());
//        throw new RuntimeException("Failed to create chat room", e);
//    }
//
//    return roomId; // 생성된 채팅방 ID 반환
//}

    public String createChatRoom(String userId1, String userId2, ChatRoomDTO chatRoomDTO) {
        // 유저 엔티티에서 username 가져오기
        User user1 = userRepository.findById(Long.valueOf(userId1)).orElse(null);
        User user2 = userRepository.findById(Long.valueOf(userId2)).orElse(null);

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("One or both users not found");
        }

        // username 기반 roomId 생성
        String roomId1 = user1.getUsername() + "_" + user2.getUsername() + "_room";
        String roomId2 = user2.getUsername() + "_" + user1.getUsername() + "_room";

        // Firebase에서 기존 방 확인
        CompletableFuture<String> existingRoomCheck = checkIfRoomExists(roomId1, roomId2);
        try {
            String existingRoomId = existingRoomCheck.get();
            if (existingRoomId != null) {
                // 이미 방이 존재하면 기존 방 ID 반환
                System.out.println("Chat room already exists with ID: " + existingRoomId);
                return existingRoomId;
            }
        } catch (Exception e) {
            System.err.println("Failed to check existing chat room: " + e.getMessage());
            throw new RuntimeException("Failed to check existing chat room", e);
        }

        // 새로운 방 생성
        chatRoomDTO.setRoomId(roomId1); // 기본적으로 roomId1 사용
        ApiFuture<Void> future = databaseReference.child(roomId1).setValueAsync(chatRoomDTO);

        try {
            // 작업 결과 확인
            future.get(); // 작업이 완료될 때까지 대기
            System.out.println("Chat room created successfully with ID: " + roomId1);
        } catch (Exception e) {
            System.err.println("Failed to create chat room: " + e.getMessage());
            throw new RuntimeException("Failed to create chat room", e);
        }

        return roomId1; // 생성된 채팅방 ID 반환
    }


//    // 채팅방 나가기
//    public void leaveRoom(String roomId, String userId) {
//        databaseReference.child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                List<String> users = getUsersFromSnapshot(snapshot);
//                if (users.contains(userId)) {
//                    users.remove(userId);
//
//                    if (users.isEmpty()) {
//                        databaseReference.child(roomId).removeValue((error, ref) -> {
//                            if (error != null) {
//                                System.err.println("Failed to remove room: " + error.getMessage());
//                            }
//                        });
//                    } else {
//                        databaseReference.child(roomId).child("users").setValue(users, (error, ref) -> {
//                            if (error != null) {
//                                System.err.println("Failed to update users: " + error.getMessage());
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                System.err.println("Failed to leave room: " + error.getMessage());
//            }
//        });
//    }
public void leaveRoom(String roomId, String userId) {
    databaseReference.child(roomId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            List<String> users = getUsersFromSnapshot(snapshot);
            if (users != null && users.contains(userId)) {
                users.remove(userId);

                // 나간 유저 정보 가져오기
                User user = userRepository.findById(Long.valueOf(userId)).orElse(null);

                // 유저가 나간 시스템 메시지 추가
                if (user != null) {
                    String systemMessageId = databaseReference.child(roomId).child("messages").push().getKey();
                    if (systemMessageId != null) {
                        ChatMessageDTO systemMessage = new ChatMessageDTO();
                        systemMessage.setMessageId(systemMessageId);
                        systemMessage.setSender("system");
                        systemMessage.setContent(user.getUsername() + "님이 나갔습니다.");
                        systemMessage.setTimestamp(System.currentTimeMillis());

                        ApiFuture<Void> future = databaseReference.child(roomId).child("messages").child(systemMessageId).setValueAsync(systemMessage);
                        future.addListener(() -> {
                            try {
                                future.get(); // 성공적으로 완료된 경우
                                System.out.println("System message added successfully");
                            } catch (Exception e) {
                                System.err.println("Failed to add system message: " + e.getMessage());
                            }
                        }, Runnable::run);
                    }
                }

                // 남은 유저 업데이트 또는 방 삭제
                if (users.isEmpty()) {
                    ApiFuture<Void> future = databaseReference.child(roomId).removeValueAsync();
                    future.addListener(() -> {
                        try {
                            future.get(); // 성공적으로 완료된 경우
                            System.out.println("Chat room removed successfully");
                        } catch (Exception e) {
                            System.err.println("Failed to remove chat room: " + e.getMessage());
                        }
                    }, Runnable::run);
                } else {
                    ApiFuture<Void> future = databaseReference.child(roomId).child("users").setValueAsync(users);
                    future.addListener(() -> {
                        try {
                            future.get(); // 성공적으로 완료된 경우
                            System.out.println("Chat room users updated successfully");
                        } catch (Exception e) {
                            System.err.println("Failed to update chat room users: " + e.getMessage());
                        }
                    }, Runnable::run);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            System.err.println("Failed to leave room: " + error.getMessage());
        }
    });
}






//    // 메시지 조회 메서드
//    public CompletableFuture<List<ChatMessageDTO>> getMessages(String roomId) {
//        CompletableFuture<List<ChatMessageDTO>> future = new CompletableFuture<>();
//        databaseReference.child(roomId).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                List<ChatMessageDTO> messages = new ArrayList<>();
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    ChatMessageDTO message = child.getValue(ChatMessageDTO.class);
//                    if (message != null) {
//                        // sender ID 를 username 으로 변환
//                        User user = userRepository.findById(Long.valueOf(message.getSender())).orElse(null);
//                        if (user != null) {
//                            message.setSender(user.getUsername()); // sender를 userid 에서 username으로 수정
//
//                        }
//                        messages.add(message);
//                    }
//                }
//                future.complete(messages); // 메시지 리스트 반환
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                future.completeExceptionally(new RuntimeException(error.getMessage()));
//            }
//        });
//        return future;
//    }
//
public CompletableFuture<List<ChatMessageDTO>> getMessages(String roomId) {
    CompletableFuture<List<ChatMessageDTO>> future = new CompletableFuture<>();
    databaseReference.child(roomId).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            List<ChatMessageDTO> messages = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                ChatMessageDTO message = child.getValue(ChatMessageDTO.class);
                if (message != null) {
                    // 타임스탬프를 변환
                    String formattedTime = TimeUtil.convertTimestampToKoreanTime(message.getTimestamp());
                    message.setFormattedTimestamp(formattedTime); // 변환된 시간 저장
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

    // Helper Method: Snapshot에서 사용자 리스트 가져오기
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

    private CompletableFuture<String> checkIfRoomExists(String roomId1, String roomId2) {
        CompletableFuture<String> future = new CompletableFuture<>();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // roomId1 또는 roomId2가 존재하는지 확인
                if (snapshot.hasChild(roomId1)) {
                    future.complete(roomId1);
                } else if (snapshot.hasChild(roomId2)) {
                    future.complete(roomId2);
                } else {
                    future.complete(null); // 존재하지 않으면 null 반환
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
