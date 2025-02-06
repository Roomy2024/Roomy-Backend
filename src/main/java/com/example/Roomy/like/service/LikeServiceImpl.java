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
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    @Override
    @Transactional
    public void toggleLike(Long id, Long userId, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LikeEntity likeEntity = null;

        switch (type.toLowerCase()) {
            case "community":
                CommunityEntity community = communityRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Community not found"));
                likeEntity = likeRepository.findByCommunityAndUser(community, user).orElse(null);

                if (likeEntity != null) {
                    likeRepository.delete(likeEntity);
                } else {
                    likeEntity = LikeEntity.builder()
                            .community(community)
                            .user(user)
                            .build();
                    likeRepository.save(likeEntity);
                }
                break;

            case "comment":
                CommentEntity comment = commentRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
                likeEntity = likeRepository.findByCommentAndUser(comment, user).orElse(null);

                if (likeEntity != null) {
                    likeRepository.delete(likeEntity);
                } else {
                    likeEntity = LikeEntity.builder()
                            .comment(comment)
                            .user(user)
                            .build();
                    likeRepository.save(likeEntity);
                }
                break;

            case "reply":
                ReplyEntity reply = replyRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Reply not found"));
                likeEntity = likeRepository.findByReplyAndUser(reply, user).orElse(null);

                if (likeEntity != null) {
                    likeRepository.delete(likeEntity);
                } else {
                    likeEntity = LikeEntity.builder()
                            .reply(reply)
                            .user(user)
                            .build();
                    likeRepository.save(likeEntity);
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid type. Must be 'community', 'comment', or 'reply'.");
        }
    }




    @Override
    public long countLikes(Long id, String type) {
        switch (type.toLowerCase()) {
            case "community":
                return likeRepository.countByCommunity_CommunityId(id);
            case "comment":
                return likeRepository.countByComment_CommentId(id);
            case "reply":
                return likeRepository.countByReply_ReplyId(id);
            default:
                throw new IllegalArgumentException("Invalid type. Must be 'community', 'comment', or 'reply'.");
        }
    }

    @Override
    public boolean isLikedByUser(Long id, Long userId, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        switch (type.toLowerCase()) {
            case "community":
                CommunityEntity community = communityRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Community not found"));
                return likeRepository.findByCommunityAndUser(community, user).isPresent();

            case "comment":
                CommentEntity comment = commentRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
                return likeRepository.findByCommentAndUser(comment, user).isPresent();

            case "reply":
                ReplyEntity reply = replyRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Reply not found"));
                return likeRepository.findByReplyAndUser(reply, user).isPresent();

            default:
                throw new IllegalArgumentException("Invalid type. Must be 'community', 'comment', or 'reply'.");
        }
    }
}
