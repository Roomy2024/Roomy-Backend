package com.example.Roomy.like.service;

import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.community.repository.CommunityRepository;
import com.example.Roomy.like.entity.LikeEntity;
import com.example.Roomy.like.repository.LikeRepository;
import com.example.Roomy.like.service.LikeService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeSerivceimpl extends LikeService {

    private final LikeRepository likeRepository;
    private final CommunityRepository communityRepository;

    @Override
    @Transactional
    public void like(Long communityId, Long UserId){
        CommunityEntity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    // 좋아요 중복 방지

        if(likeRepository.findByCommunity_CommunityIdAndUserId(communityId, userId).isPresent()){
            throw new IllegalStateException("이미 좋아요 누른 게시글 입니다.");
        }

        LikeEntity likeEntity = LikeEntity.builder()
                .community(community)
                .userId(userId)
                .build();


        likeRepository.save(likeEntity);
        System.out.println("좋아요" + communityId + "아이디" + userId);
    }

}
