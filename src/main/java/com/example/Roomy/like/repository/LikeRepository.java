package com.example.Roomy.like.repository;

import com.example.Roomy.SocialLogin.Model.User;
import com.example.Roomy.comment.entity.CommentEntity;
import com.example.Roomy.comment.entity.ReplyEntity;
import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.like.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByCommunityAndUser(CommunityEntity community, User user); // 수정된 메서드
    Optional<LikeEntity> findByCommentAndUser(CommentEntity comment, User user);
    Optional<LikeEntity> findByReplyAndUser(ReplyEntity reply, User user);
    long countByCommunity_CommunityId(Long communityId); // 커뮤니티 좋아요 수
    long countByComment_CommentId(Long commentId); // 댓글 좋아요 수
    long countByReply_ReplyId(Long replyId); // 대댓글 좋아요 수
}
