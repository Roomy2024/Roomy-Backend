package com.example.Roomy.chat.dto;

import java.util.List;

public class ChatRoomDTO {
    private String roomId; // 채팅방 ID
    private List<String> users; // 사용자 ID 목록

    // 기본 생성자 (Firebase 직렬화용)
    public ChatRoomDTO() {
    }

    // 모든 필드를 포함하는 생성자
    public ChatRoomDTO(String roomId, List<String> users) {
        this.roomId = roomId;
        this.users = users;
    }

    // Getter 및 Setter
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
