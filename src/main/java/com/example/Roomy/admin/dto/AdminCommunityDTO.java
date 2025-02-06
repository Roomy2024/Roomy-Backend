package com.example.Roomy.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCommunityDTO {
    private Long id;
    private String title;
    private String authorEmail;
    private LocalDateTime createdAt;
    private int views;
}
