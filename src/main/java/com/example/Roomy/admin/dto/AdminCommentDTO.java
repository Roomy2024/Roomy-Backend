package com.example.Roomy.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCommentDTO {
    private Long commentId;
    private String content;
    private String authorEmail;
}
