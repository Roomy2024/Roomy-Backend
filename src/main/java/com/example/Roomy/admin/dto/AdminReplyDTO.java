package com.example.Roomy.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminReplyDTO {
    private Long replyId;
    private String content;
    private String authorEmail;
}
