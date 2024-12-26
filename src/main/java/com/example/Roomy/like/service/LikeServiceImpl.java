package com.example.Roomy.like.service;

import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.community.repository.CommunityRepository;
import com.example.Roomy.like.entity.LikeEntity;
import com.example.Roomy.like.repository.LikeRepository;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void like(Long communityId, Long userId) {
        CommunityEntity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (likeRepository.findByCommunity_CommunityIdAndUser_Id(communityId, userId).isPresent()) {
            throw new IllegalStateException("Already liked this community");
        }

        LikeEntity likeEntity = LikeEntity.builder()
                .community(community)
                .user(user)
                .build();

        likeRepository.save(likeEntity);
        System.out.println("좋아요 추가: communityId=" + communityId + ", userId=" + userId);
    }

    @Override
    @Transactional
    public void unlike(Long communityId, Long userId) {
        LikeEntity likeEntity = likeRepository.findByCommunity_CommunityIdAndUser_Id(communityId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Like not found"));

        likeRepository.delete(likeEntity);
        System.out.println("좋아요 제거: communityId=" + communityId + ", userId=" + userId);
    }

    @Override
    public boolean isLikedByUser(Long communityId, Long userId) {
        return likeRepository.findByCommunity_CommunityIdAndUser_Id(communityId, userId).isPresent();
    }


    @Override
    public long countLikes(Long communityId) {
        return likeRepository.countByCommunity_CommunityId(communityId);
    }
}
