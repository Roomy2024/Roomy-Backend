package com.example.Roomy.community.repository;

import com.example.Roomy.community.entity.CommunityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityEntity, Long> {

    @Modifying
    @Query("UPDATE CommunityEntity c SET c.views = c.views + 1 WHERE c.communityId = :id")
    void increaseViewCount(@Param("id") Long id);

    @Query("SELECT COUNT(l) FROM LikeEntity l WHERE l.community.communityId = :communityId")
    int countLikes(@Param("communityId") Long communityId);

    @Query("SELECT c FROM CommunityEntity c WHERE c.author.id = :userId")
    List<CommunityEntity> findByAuthorId(@Param("userId") Long userId);

    Page<CommunityEntity> findAll(Pageable pageable);
}
