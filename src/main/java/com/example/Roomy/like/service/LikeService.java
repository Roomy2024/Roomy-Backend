package com.example.Roomy.like.service;

public interface LikeService {
    int toggleLike(Long communityId, Long userId); // 좋아요 토글 후 현재 상태 반환
    long countLikes(Long communityId); // 좋아요 개수 조회
    int getLikeStatus(Long communityId, Long userId); // 현재 사용자의 좋아요 상태 (0 또는 1)
}
