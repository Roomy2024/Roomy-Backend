package com.example.Roomy.bookmark.controller;

import com.example.Roomy.bookmark.dto.BookmarkDTO;
import com.example.Roomy.bookmark.service.BookmarkService;
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
     *
     * @param userId       유저 ID
     * @param communityId  커뮤니티 ID
     * @return 성공 여부 (true: 북마크 추가, false: 북마크 제거)
     */
    @PostMapping("/{userId}/{communityId}/toggle")
    public ResponseEntity<Boolean> toggleBookmark(
            @PathVariable Long userId,
            @PathVariable Long communityId
    ) {
        boolean result = bookmarkService.toggleBookmark(userId, communityId);
        return ResponseEntity.ok(result);
    }

    /**
     * 특정 유저의 북마크 리스트 조회
     *
     * @param userId 유저 ID
     * @return 북마크 리스트
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<BookmarkDTO>> getUserBookmarks(@PathVariable Long userId) {
        List<BookmarkDTO> bookmarks = bookmarkService.getBookmarksByUser(userId);
        return ResponseEntity.ok(bookmarks);
    }

    /**
     * 특정 커뮤니티에 대한 북마크 여부 확인
     *
     * @param communityId 커뮤니티 ID
     * @param userId      유저 ID
     * @return 북마크 여부 (true: 북마크 존재, false: 북마크 없음)
     */
    @GetMapping("/{communityId}/user/{userId}")
    public ResponseEntity<Boolean> isBookmarkedByUser(
            @PathVariable Long communityId,
            @PathVariable Long userId
    ) {
        boolean isBookmarked = bookmarkService.isBookmarkedByUser(communityId, userId);
        return ResponseEntity.ok(isBookmarked);
    }
}
