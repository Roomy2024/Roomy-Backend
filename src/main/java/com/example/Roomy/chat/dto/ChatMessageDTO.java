package com.example.Roomy.chat.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private String messageId;
    private String sender;
    private String content;
    private long timestamp; // 기본 타임스탬프
    private String formattedTimestamp; // 변환된 시간 문자열 저장

    public void setFormattedTimestamp(String formattedTimestamp) {
        this.formattedTimestamp = formattedTimestamp;
    }
}
