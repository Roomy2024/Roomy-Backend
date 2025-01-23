package com.example.Roomy.bookmark.service;

import com.example.Roomy.bookmark.dto.BookmarkDTO;

import java.util.List;

public interface BookmarkService {
    boolean toggleBookmark(Long userId, Long communityId); // 북마크 추가/제거 토글
    List<BookmarkDTO> getBookmarksByUser(Long userId); // 유저의 북마크 조회
    boolean isBookmarkedByUser(Long communityId, Long userId); // 북마크 여부 확인
}
