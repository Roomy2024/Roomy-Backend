package com.example.Roomy.notification.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationDTO {

    private Long id;
    private String sender;
    private String message;
    private LocalDateTime createdAt;
}
