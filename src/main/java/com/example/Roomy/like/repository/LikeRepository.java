package com.example.Roomy.like.repository;

import com.example.Roomy.like.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByCommunity_CommunityIdAndUserId(Long communityId, Long UserId);
    long countByCommunity_CommunityId(Long communityId);

}
