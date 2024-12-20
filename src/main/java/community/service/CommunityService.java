package community.service;

import community.domain.CommunityEntity;
import community.domain.CommunityRequestDTO;
import community.domain.CommunityResponseDTO;
import community.dao.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;

    // 생성
    public CommunityResponseDTO createCommunity(CommunityRequestDTO requestDTO) {
        CommunityEntity entity = requestDTO.toEntity();
        CommunityEntity savedEntity = communityRepository.save(entity);
        return savedEntity.toResponseDTO();
    }

    // 단일 조회
    public CommunityResponseDTO getCommunityById(Long id) {
        CommunityEntity entity = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Community not found with ID: " + id));
        return entity.toResponseDTO();
    }

    // 전체 조회
    public List<CommunityResponseDTO> getAllCommunities() {
        return communityRepository.findAll()
                .stream()
                .map(CommunityEntity::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 업데이트
    public CommunityResponseDTO updateCommunity(Long id, CommunityRequestDTO requestDTO) {
        CommunityEntity entity = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Community not found with ID: " + id));

        // 작성자 검증 로직 추가 (유저 엔티티 생성 시 사용)
        // if (!entity.getAuthor().getId().equals(requestDTO.getAuthorId())) {
        //     throw new IllegalStateException("Only the author can update this community.");
        // }

        entity.update(requestDTO.getTitle(), requestDTO.getContent(), requestDTO.getType());
        CommunityEntity updatedEntity = communityRepository.save(entity);
        return updatedEntity.toResponseDTO();
    }

    // 삭제
    public void deleteCommunity(Long id) {
        // 작성자 검증 로직 추가 (유저 엔티티 생성 시 사용)
        // CommunityEntity entity = communityRepository.findById(id)
        //         .orElseThrow(() -> new IllegalArgumentException("Community not found with ID: " + id));
        // if (!entity.getAuthor().getId().equals(currentUserId)) {
        //     throw new IllegalStateException("Only the author can delete this community.");
        // }

        communityRepository.deleteById(id);
    }
}
