package com.example.Roomy.comment.repository;

import com.example.Roomy.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByCommunity_CommunityId(Long communityId);
}
