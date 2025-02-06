package com.example.Roomy.bookmark.repository;

import com.example.Roomy.bookmark.entity.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {
    List<BookmarkEntity> findByUserId(Long userId);

    // 'CommunityEntity'의 'communityId' 필드를 명시적으로 참조하도록 수정
    Optional<BookmarkEntity> findByUserIdAndCommunity_CommunityId(Long userId, Long communityId);

    void deleteByCommunity_CommunityId(Long communityId);
}
