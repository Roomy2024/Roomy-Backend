package com.example.Roomy.like.service;

public interface LikeService {

    void like(Long communityId, long userId); // 좋아요 추가
    void unlike(Long communityId, long userId);
    long countLikes(Long communityId);
}
