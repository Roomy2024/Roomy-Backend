package com.example.Roomy.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {
    private Long communityId; // 댓글이 속한 커뮤니티 ID
    private Long userId;      // 댓글 작성자 ID
    private String content;   // 댓글 내용
}
