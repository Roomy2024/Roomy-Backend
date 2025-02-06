package com.example.Roomy.like.controller;

import com.example.Roomy.like.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 토글", description = "커뮤니티 게시글의 좋아요를 추가 또는 제거합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{communityId}/like-toggle")
    public ResponseEntity<Integer> toggleLike(
            @PathVariable Long communityId,
            @RequestParam Long userId) {
        int likeStatus = likeService.toggleLike(communityId, userId);
        return ResponseEntity.ok(likeStatus);
    }

    @Operation(summary = "좋아요 개수 조회", description = "커뮤니티 게시글의 좋아요 개수를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 개수 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 게시글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{communityId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long communityId) {
        long count = likeService.countLikes(communityId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "좋아요 여부 확인", description = "사용자가 게시글에 좋아요를 눌렀는지 확인합니다.")
    @GetMapping("/{communityId}/is-liked")
    public ResponseEntity<Integer> isLikedByUser(
            @PathVariable Long communityId,
            @RequestParam Long userId) {
        int likeStatus = likeService.getLikeStatus(communityId, userId);
        return ResponseEntity.ok(likeStatus);
    }
}
