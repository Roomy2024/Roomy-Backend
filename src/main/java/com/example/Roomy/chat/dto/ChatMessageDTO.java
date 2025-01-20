package com.example.Roomy.chat.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private String messageId;
    private String sender;
    private String content;
    private long timestamp; // 기존 타임스탬프
    private String formattedTimestamp; // 변환된 한국 시간

    // Getter 및 Setter
    public String getFormattedTimestamp() {
        return formattedTimestamp;
    }

    public void setFormattedTimestamp(String formattedTimestamp) {
        this.formattedTimestamp = formattedTimestamp;
    }
}
