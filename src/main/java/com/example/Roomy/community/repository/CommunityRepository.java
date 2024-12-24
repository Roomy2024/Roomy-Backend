package com.example.Roomy.community.repository;

import com.example.Roomy.community.entity.CommunityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<CommunityEntity, Long> {
}
