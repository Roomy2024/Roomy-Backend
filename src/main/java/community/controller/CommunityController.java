package community.controller;

import community.domain.CommunityRequestDTO;
import community.domain.CommunityResponseDTO;
import community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping("/create")
    public ResponseEntity<CommunityResponseDTO> createCommunity(@RequestBody CommunityRequestDTO requestDTO) {
        CommunityResponseDTO responseDTO = communityService.createCommunity(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CommunityResponseDTO> updateCommunity(
            @PathVariable Long id,
            @RequestBody CommunityRequestDTO requestDTO
    ) {
        CommunityResponseDTO responseDTO = communityService.updateCommunity(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CommunityResponseDTO>> getAllCommunities() {
        List<CommunityResponseDTO> responseList = communityService.getAllCommunities();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponseDTO> getCommunityById(@PathVariable Long id) {
        CommunityResponseDTO responseDTO = communityService.getCommunityById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCommunity(@PathVariable Long id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.ok("Community deleted successfully");
    }
}
