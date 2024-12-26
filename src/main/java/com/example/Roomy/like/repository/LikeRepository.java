package com.example.Roomy.like.repository;

import com.example.Roomy.like.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByCommunity_CommunityIdAndUser_Id(Long communityId, Long userId);
    long countByCommunity_CommunityId(Long communityId);
}
