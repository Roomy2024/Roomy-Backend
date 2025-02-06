package com.example.Roomy.comment.repository;

import com.example.Roomy.comment.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {
    List<ReplyEntity> findByParentComment_CommentId(Long commentId);
    void deleteByParentComment_CommentId(Long commentId);

    @Query("SELECT COUNT(r) FROM ReplyEntity r WHERE r.parentComment.community.communityId = :communityId")
    int countRepliesByCommunityId(@Param("communityId") Long communityId);

}
