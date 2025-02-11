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
import com.example.Roomy.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public int toggleLike(Long communityId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CommunityEntity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        LikeEntity likeEntity = likeRepository.findByCommunityAndUser(community, user).orElse(null);

        if (likeEntity != null) {
            likeEntity.setIsLiked(likeEntity.getIsLiked() == 1 ? 0 : 1);
            likeRepository.save(likeEntity);

            // 본인이 자신의 글에 좋아요를 눌렀을 경우 알림을 보내지 않음
            if (likeEntity.getIsLiked() == 1 && !user.getId().equals(community.getAuthor().getId())) {
                notificationService.sendNotification( // ✅ 인스턴스 메서드 호출
                        user.getId(),
                        community.getAuthor().getId(),
                        "[좋아요 알림] " + user.getUsername() + "님이 " + community.getTitle() + " 글에 좋아요를 눌렀습니다."
                );
            }

            return likeEntity.getIsLiked();
        } else {
            likeEntity = LikeEntity.builder()
                    .community(community)
                    .user(user)
                    .isLiked(1)
                    .build();
            likeRepository.save(likeEntity);

            if (!user.getId().equals(community.getAuthor().getId())) {
                notificationService.sendNotification( // ✅ 인스턴스 메서드 호출
                        user.getId(),
                        community.getAuthor().getId(),
                        "[좋아요 알림] " + user.getUsername() + "님이 " + community.getTitle() + " 글에 좋아요를 눌렀습니다."
                );
            }

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
