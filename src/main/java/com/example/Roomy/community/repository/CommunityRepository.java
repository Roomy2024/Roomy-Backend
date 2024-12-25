package com.example.Roomy.community.repository;

import com.example.Roomy.community.entity.CommunityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityEntity, Long> {

    @Modifying
    @Query("UPDATE CommunityEntity c SET c.views = c.views + 1 WHERE c.communityId = :id")
    void increaseViewCount(@Param("id") Long id);

}
