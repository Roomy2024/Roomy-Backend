package com.example.Roomy.community.service;

import com.example.Roomy.community.dto.CommunityRequestDTO;
import com.example.Roomy.community.dto.CommunityResponseDTO;

import java.io.IOException;
import java.util.List;

public interface CommunityService {
    CommunityResponseDTO createCommunity(CommunityRequestDTO communityRequestDTO) throws IOException;
    CommunityResponseDTO updateCommunity(Long id, CommunityRequestDTO communityRequestDTO) throws IOException;
    CommunityResponseDTO getCommunity(Long id);
    List<CommunityResponseDTO> getAllCommunities();
    String deleteCommunity(Long id);
}
