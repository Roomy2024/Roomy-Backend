package com.example.Roomy.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReplyResponseDTO {
    private Long replyId;       // 대댓글 ID
    private Long commentId;     // 대댓글이 속한 부모 댓글 ID
    private String author;      // 대댓글 작성자 이름
    private String content;     // 대댓글 내용
    private LocalDateTime createdAt; // 대댓글 작성 시간
    private LocalDateTime updatedAt; // 대댓글 수정 시간
}
