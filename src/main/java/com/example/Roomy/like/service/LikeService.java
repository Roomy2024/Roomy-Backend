package com.example.Roomy.like.service;

public interface LikeService {

    void toggleLike(Long id, Long userId, String type);
    long countLikes(Long id, String type); // 좋아요 개수 조회
    boolean isLikedByUser(Long id, Long userId, String type); // 특정 사용자 좋아요 여부 확인

}
