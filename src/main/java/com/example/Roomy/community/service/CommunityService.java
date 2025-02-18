package com.example.Roomy.community.service;

import com.example.Roomy.community.dto.CommunityRequestDTO;
import com.example.Roomy.community.dto.CommunityResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface CommunityService {
    CommunityResponseDTO createCommunity(CommunityRequestDTO communityRequestDTO) throws IOException;
    CommunityResponseDTO updateCommunity(Long id, CommunityRequestDTO communityRequestDTO) throws IOException;
    CommunityResponseDTO getCommunity(Long id);
    Page<CommunityResponseDTO> getAllCommunities(String type, Pageable pageable);
    String deleteCommunity(Long id, Long userId);
    void increaseViewCount(Long id);
    List<CommunityResponseDTO> getMyCommunities(Long userId);
    int getTotalCommentCount(Long communityId);
}
