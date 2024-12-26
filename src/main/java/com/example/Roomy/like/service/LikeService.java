package com.example.Roomy.like.service;

public interface LikeService {
    void like(Long communityId, Long userId);
    void unlike(Long communityId, Long userId);
    long countLikes(Long communityId);
    boolean isLikedByUser(Long communityId, Long userId);
}
