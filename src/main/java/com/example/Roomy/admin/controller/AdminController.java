package com.example.Roomy.admin.controller;

import com.example.Roomy.admin.dto.AdminCommunityDTO;
import com.example.Roomy.admin.dto.AdminCommentDTO;
import com.example.Roomy.admin.dto.AdminReplyDTO;
import com.example.Roomy.admin.dto.AdminChatMessageDTO;
import com.example.Roomy.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 게시글 전체 조회
    @GetMapping("/posts")
    public ResponseEntity<List<AdminCommunityDTO>> getAllPosts() {
        return ResponseEntity.ok(adminService.getAllPosts());
    }

    // 게시글 상세 보기
    @GetMapping("/posts/{postId}")
    public ResponseEntity<AdminCommunityDTO> getPostDetail(@PathVariable Long postId) {
        return ResponseEntity.ok(adminService.getPostDetail(postId));
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        adminService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // 게시글의 댓글 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<AdminCommentDTO>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(adminService.getCommentsByPostId(postId));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        adminService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 대댓글 조회
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<List<AdminReplyDTO>> getReplies(@PathVariable Long commentId) {
        return ResponseEntity.ok(adminService.getRepliesByCommentId(commentId));
    }

    // 대댓글 삭제
    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId) {
        adminService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }

    // 채팅 목록 조회 (Firebase 그룹채팅)
    @GetMapping("/groupchat/{roomId}/messages")
    public ResponseEntity<List<AdminChatMessageDTO>> getGroupChatMessages(@PathVariable String roomId) {
        return ResponseEntity.ok(adminService.getGroupChatMessages(roomId));
    }

    // 채팅 메시지 삭제 (Firebase 그룹채팅)
    @DeleteMapping("/groupchat/{roomId}/message/{messageId}")
    public ResponseEntity<Void> deleteGroupChatMessage(@PathVariable String roomId, @PathVariable String messageId) {
        adminService.deleteGroupChatMessage(roomId, messageId);
        return ResponseEntity.noContent().build();
    }

    // 채팅 목록 조회 (Firebase 1:1 채팅)
    @GetMapping("/onetoonechat/{roomId}/messages")
    public ResponseEntity<List<AdminChatMessageDTO>> getPrivateChatMessages(@PathVariable String roomId) {
        return ResponseEntity.ok(adminService.getPrivateChatMessages(roomId));
    }

    // 채팅 메시지 삭제 (Firebase 1:1 채팅)
    @DeleteMapping("/onetoonechat/{roomId}/message/{messageId}")
    public ResponseEntity<Void> deletePrivateChatMessage(@PathVariable String roomId, @PathVariable String messageId) {
        adminService.deletePrivateChatMessage(roomId, messageId);
        return ResponseEntity.noContent().build();
    }
}
