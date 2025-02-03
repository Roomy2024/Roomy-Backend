package com.example.Roomy.bookmark.service;

import com.example.Roomy.SocialLogin.UserRepository;
import com.example.Roomy.bookmark.dto.BookmarkDTO;
import com.example.Roomy.bookmark.entity.BookmarkEntity;
import com.example.Roomy.bookmark.repository.BookmarkRepository;
import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.community.repository.CommunityRepository;
import com.example.Roomy.SocialLogin.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    @Override
    public boolean toggleBookmark(Long userId, Long communityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        CommunityEntity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        // 기존 북마크 여부 확인
        BookmarkEntity existingBookmark = bookmarkRepository.findByUserIdAndCommunity_CommunityId(userId, communityId)
                .orElse(null);

        if (existingBookmark != null) {
            // 기존 북마크가 존재하면 삭제
            bookmarkRepository.delete(existingBookmark);
            return false; // 북마크 삭제됨
        } else {
            // 북마크가 없으면 추가
            BookmarkEntity newBookmark = BookmarkEntity.builder()
                    .user(user)
                    .community(community)
                    .active(true)
                    .build();
            bookmarkRepository.save(newBookmark);
            return true; // 북마크 추가됨
        }
    }

    @Override
    public List<BookmarkDTO> getBookmarksByUser(Long userId) {
        return bookmarkRepository.findByUserId(userId).stream()
                .map(bookmark -> {
                    BookmarkDTO dto = new BookmarkDTO();
                    dto.setId(bookmark.getId());
                    dto.setUserId(bookmark.getUser().getId());
                    dto.setCommunityId(bookmark.getCommunity().getCommunityId());
                    dto.setActive(bookmark.isActive());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isBookmarkedByUser(Long communityId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        CommunityEntity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다."));

        return bookmarkRepository.findByUserIdAndCommunity_CommunityId(userId, communityId).isPresent();
    }
}
