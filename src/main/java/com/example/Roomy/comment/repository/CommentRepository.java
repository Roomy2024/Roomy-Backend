package com.example.Roomy.comment.repository;

import com.example.Roomy.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByCommunity_CommunityId(Long communityId);
    void deleteByCommunity_CommunityId(Long communityId);

    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.community.communityId = :communityId")
    int countByCommunityId(@Param("communityId") Long communityId);

}
