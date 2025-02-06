package com.example.Roomy.like.service;

import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.comment.entity.CommentEntity;
import com.example.Roomy.comment.entity.ReplyEntity;
import com.example.Roomy.comment.repository.CommentRepository;
import com.example.Roomy.comment.repository.ReplyRepository;
import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.community.repository.CommunityRepository;
import com.example.Roomy.like.entity.LikeEntity;
import com.example.Roomy.like.repository.LikeRepository;
import com.example.Roomy.SocialLogin.Entity.User;
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
    public int toggleLike(Long communityId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CommunityEntity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        LikeEntity likeEntity = likeRepository.findByCommunityAndUser(community, user).orElse(null);

        if (likeEntity != null) {
            // 기존 좋아요가 있다면 상태를 반전
            likeEntity.setIsLiked(likeEntity.getIsLiked() == 1 ? 0 : 1);
            likeRepository.save(likeEntity);
            return likeEntity.getIsLiked();
        } else {
            // 기존 기록이 없다면 새로 생성
            likeEntity = LikeEntity.builder()
                    .community(community)
                    .user(user)
                    .isLiked(1) // 처음 누르면 1
                    .build();
            likeRepository.save(likeEntity);
            return 1;
        }
    }

    @Override
    public long countLikes(Long communityId) {
        return likeRepository.countByCommunity_CommunityId(communityId);
    }

    @Override
    public int getLikeStatus(Long communityId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CommunityEntity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        return likeRepository.findByCommunityAndUser(community, user)
                .map(LikeEntity::getIsLiked)
                .orElse(0);
    }
}
