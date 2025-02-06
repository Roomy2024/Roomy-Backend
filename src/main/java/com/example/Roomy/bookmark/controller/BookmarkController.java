package com.example.Roomy.bookmark.controller;

import com.example.Roomy.bookmark.dto.BookmarkDTO;
import com.example.Roomy.bookmark.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * 북마크 추가 또는 제거
     */
    @Operation(summary = "북마크 추가/제거", description = "사용자가 특정 커뮤니티 게시글을 북마크 추가하거나 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 추가 또는 제거 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{userId}/{communityId}/toggle")
    public ResponseEntity<Boolean> toggleBookmark(
            @Parameter(description = "유저 ID") @PathVariable Long userId,
            @Parameter(description = "커뮤니티 ID") @PathVariable Long communityId
    ) {
        boolean result = bookmarkService.toggleBookmark(userId, communityId);
        return ResponseEntity.ok(result);
    }

    /**
     * 특정 유저의 북마크 리스트 조회
     */
    @Operation(summary = "사용자의 북마크 목록 조회", description = "특정 사용자가 저장한 북마크 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 사용자의 북마크가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<BookmarkDTO>> getUserBookmarks(
            @Parameter(description = "유저 ID") @PathVariable Long userId) {
        List<BookmarkDTO> bookmarks = bookmarkService.getBookmarksByUser(userId);
        return ResponseEntity.ok(bookmarks);
    }

    /**
     * 특정 커뮤니티에 대한 북마크 여부 확인
     */
    @Operation(summary = "북마크 여부 확인", description = "사용자가 특정 커뮤니티 게시글을 북마크했는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 여부 반환 성공"),
            @ApiResponse(responseCode = "404", description = "북마크 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{communityId}/user/{userId}")
    public ResponseEntity<Boolean> isBookmarkedByUser(
            @Parameter(description = "커뮤니티 ID") @PathVariable Long communityId,
            @Parameter(description = "유저 ID") @PathVariable Long userId
    ) {
        boolean isBookmarked = bookmarkService.isBookmarkedByUser(communityId, userId);
        return ResponseEntity.ok(isBookmarked);
    }
}
