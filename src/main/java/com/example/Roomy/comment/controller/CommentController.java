package com.example.Roomy.comment.controller;

import com.example.Roomy.comment.dto.*;
import com.example.Roomy.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/create")
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO createdComment = commentService.addComment(commentRequestDTO);
        return ResponseEntity.ok(createdComment);
    }

    // 대댓글 생성
    @PostMapping("/reply")
    public ResponseEntity<ReplyResponseDTO> createReply(@RequestBody ReplyRequestDTO replyRequestDTO) {
        ReplyResponseDTO createdReply = commentService.addReply(replyRequestDTO);
        return ResponseEntity.ok(createdReply);
    }

    // 특정 커뮤니티의 댓글 조회
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByCommunityId(@PathVariable Long communityId) {
        List<CommentResponseDTO> comments = commentService.getCommentsByCommunityId(communityId);
        return ResponseEntity.ok(comments);
    }

    // 특정 댓글의 대댓글 조회
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<ReplyResponseDTO>> getRepliesByCommentId(@PathVariable Long commentId) {
        List<ReplyResponseDTO> replies = commentService.getRepliesByCommentId(commentId);
        return ResponseEntity.ok(replies);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
    }

    // 대댓글 삭제
    @DeleteMapping("/reply/{replyId}")
    public ResponseEntity<String> deleteReply(
            @PathVariable Long replyId,
            @RequestParam Long userId) {
        commentService.deleteReply(replyId, userId);
        return ResponseEntity.ok("대댓글이 성공적으로 삭제되었습니다.");
    }

    @PostMapping("/update/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateRequestDTO updateRequestDTO) {
        commentService.updateComment(commentId, updateRequestDTO);
        return ResponseEntity.ok("댓글이 성공적으로 수정되었습니다.");
    }

    @PostMapping("/reply/update/{replyId}")
    public ResponseEntity<String> updateReply(
            @PathVariable Long replyId,
            @RequestBody UpdateRequestDTO updateRequestDTO) {
        commentService.updateReply(replyId, updateRequestDTO);
        return ResponseEntity.ok("대댓글이 성공적으로 수정되었습니다.");
    }
}
