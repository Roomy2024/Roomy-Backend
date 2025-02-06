package com.example.Roomy.comment.controller;

import com.example.Roomy.comment.dto.*;
import com.example.Roomy.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 생성", description = "새로운 댓글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/create")
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO createdComment = commentService.addComment(commentRequestDTO);
        return ResponseEntity.ok(createdComment);
    }

    @Operation(summary = "대댓글 생성", description = "특정 댓글에 대한 대댓글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대댓글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/reply")
    public ResponseEntity<ReplyResponseDTO> createReply(@RequestBody ReplyRequestDTO replyRequestDTO) {
        ReplyResponseDTO createdReply = commentService.addReply(replyRequestDTO);
        return ResponseEntity.ok(createdReply);
    }

    @Operation(summary = "특정 커뮤니티의 댓글 조회", description = "특정 커뮤니티에 등록된 모든 댓글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "댓글이 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByCommunityId(
            @Parameter(description = "커뮤니티 ID") @PathVariable Long communityId) {
        List<CommentResponseDTO> comments = commentService.getCommentsByCommunityId(communityId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "특정 댓글의 대댓글 조회", description = "특정 댓글에 등록된 모든 대댓글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대댓글 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "대댓글이 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<ReplyResponseDTO>> getRepliesByCommentId(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {
        List<ReplyResponseDTO> replies = commentService.getRepliesByCommentId(commentId);
        return ResponseEntity.ok(replies);
    }

    @Operation(summary = "댓글 삭제", description = "특정 사용자가 댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (댓글 작성자가 아님)"),
            @ApiResponse(responseCode = "404", description = "댓글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "대댓글 삭제", description = "특정 사용자가 대댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대댓글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (대댓글 작성자가 아님)"),
            @ApiResponse(responseCode = "404", description = "대댓글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/reply/{replyId}")
    public ResponseEntity<String> deleteReply(
            @Parameter(description = "대댓글 ID") @PathVariable Long replyId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        commentService.deleteReply(replyId, userId);
        return ResponseEntity.ok("대댓글이 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "댓글 수정", description = "특정 사용자가 댓글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (댓글 작성자가 아님)"),
            @ApiResponse(responseCode = "404", description = "댓글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/update/{commentId}")
    public ResponseEntity<String> updateComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @RequestBody UpdateRequestDTO updateRequestDTO) {
        commentService.updateComment(commentId, updateRequestDTO);
        return ResponseEntity.ok("댓글이 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "대댓글 수정", description = "특정 사용자가 대댓글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대댓글 수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (대댓글 작성자가 아님)"),
            @ApiResponse(responseCode = "404", description = "대댓글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/reply/update/{replyId}")
    public ResponseEntity<String> updateReply(
            @Parameter(description = "대댓글 ID") @PathVariable Long replyId,
            @RequestBody UpdateRequestDTO updateRequestDTO) {
        commentService.updateReply(replyId, updateRequestDTO);
        return ResponseEntity.ok("대댓글이 성공적으로 수정되었습니다.");
    }
}
