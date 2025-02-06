package com.example.Roomy.like.repository;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.like.entity.LikeEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByCommunityAndUser(CommunityEntity community, User user);
    long countByCommunity_CommunityId(Long communityId);

    @Modifying
    @Transactional
    @Query("DELETE FROM LikeEntity l WHERE l.community = :community")
    void deleteAllByCommunity(@org.springframework.data.repository.query.Param("community") CommunityEntity community);
}
