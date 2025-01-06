package com.example.Roomy.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyRequestDTO {
    private Long commentId; // 대댓글이 속한 부모 댓글 ID
    private Long userId;    // 대댓글 작성자 ID
    private String content; // 대댓글 내용
}
