package com.example.Roomy.community.controller;

import com.example.Roomy.community.dto.CommunityRequestDTO;
import com.example.Roomy.community.dto.CommunityResponseDTO;
import com.example.Roomy.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping("/create")
    public ResponseEntity<CommunityResponseDTO> createCommunity(@ModelAttribute CommunityRequestDTO communityRequestDTO) throws IOException {
        System.out.println(communityRequestDTO);
        return ResponseEntity.ok(communityService.createCommunity(communityRequestDTO));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CommunityResponseDTO> updateCommunity(
            @PathVariable Long id,
            @ModelAttribute CommunityRequestDTO communityRequestDTO
    ) throws IOException {
        return ResponseEntity.ok(communityService.updateCommunity(id, communityRequestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponseDTO> getCommunity(@PathVariable Long id) {
        communityService.increaseViewCount(id);
        return ResponseEntity.ok(communityService.getCommunity(id));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<CommunityResponseDTO>> getAllCommunities() {
        return ResponseEntity.ok(communityService.getAllCommunities());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCommunity(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(communityService.deleteCommunity(id, userId));
    }
}
