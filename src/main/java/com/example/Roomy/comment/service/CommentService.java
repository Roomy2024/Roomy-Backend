package com.example.Roomy.comment.service;

import com.example.Roomy.comment.dto.*;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CommentService {

    // 댓글 추가
    CommentResponseDTO addComment(CommentRequestDTO commentRequestDTO);

    // 대댓글 추가
    ReplyResponseDTO addReply(ReplyRequestDTO replyRequestDTO);

    // 특정 커뮤니티의 모든 댓글 조회
    List<CommentResponseDTO> getCommentsByCommunityId(Long communityId);

    // 특정 댓글의 모든 대댓글 조회
    List<ReplyResponseDTO> getRepliesByCommentId(Long commentId);

    // 댓글 삭제
    void deleteComment(Long commentId, Long userId);

    // 대댓글 삭제
    void deleteReply(Long replyId, Long userId);

    // 댓글 수정
    void updateComment(Long commentId, UpdateRequestDTO updateRequestDTO);

    // 대댓글 수정
    void updateReply(Long replyId, UpdateRequestDTO updateRequestDTO);
}
