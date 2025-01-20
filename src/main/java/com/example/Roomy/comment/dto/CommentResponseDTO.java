package com.example.Roomy.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommentResponseDTO {
    private Long commentId;       // 댓글 ID
    private Long communityId;     // 댓글이 속한 커뮤니티 ID
    private String author;        // 댓글 작성자 이름
    private String content;       // 댓글 내용
    private LocalDateTime createdAt; // 댓글 작성 시간
    private LocalDateTime updatedAt; // 댓글 수정 시간
    private List<ReplyResponseDTO> replies; // 대댓글 리스트
}
