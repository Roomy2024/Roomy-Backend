package com.example.Roomy.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminChatMessageDTO {
    private String messageId;
    private String sender;
    private String content;
}
