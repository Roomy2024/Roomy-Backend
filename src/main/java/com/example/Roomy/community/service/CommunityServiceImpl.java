package com.example.Roomy.community.service;

import com.example.Roomy.SocialLogin.Model.User;
import com.example.Roomy.community.dto.CommunityRequestDTO;
import com.example.Roomy.community.dto.CommunityResponseDTO;
import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.community.repository.CommunityRepository;
import com.example.Roomy.image.entity.FileGroupEntity;
import com.example.Roomy.image.entity.ImageEntity;
import com.example.Roomy.image.repository.ImageRepository;
import com.example.Roomy.image.service.FileService;
import com.example.Roomy.SocialLogin.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final FileService fileService;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommunityResponseDTO createCommunity(CommunityRequestDTO communityRequestDTO) throws IOException {
        User author = userRepository.findById(communityRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CommunityEntity communityEntity = CommunityEntity.builder()
                .title(communityRequestDTO.getTitle())
                .content(communityRequestDTO.getContent())
                .type(communityRequestDTO.getType())
                .author(author) // 작성자 설정
                .build();

        FileGroupEntity fileGroup = new FileGroupEntity();
        if (communityRequestDTO.getImages() != null && !communityRequestDTO.getImages().isEmpty()) {
            for (MultipartFile file : communityRequestDTO.getImages()) {
                // 이미지 저장 및 처리
                String filePath = fileService.saveAndResizeImage(file, 800, 600); // 해상도 조정
                ImageEntity imageEntity = ImageEntity.builder()
                        .imageUrl(filePath)
                        .fileGroup(fileGroup)
                        .build();
                fileGroup.getImages().add(imageEntity);
            }
        }
        communityEntity.setFileGroupEntity(fileGroup);
        communityRepository.save(communityEntity);
        return toResponseDTO(communityEntity);
    }



    @Override
    @Transactional
    public CommunityResponseDTO updateCommunity(Long id, CommunityRequestDTO communityRequestDTO) throws IOException {
        CommunityEntity communityEntity = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        // 작성자 검증
        if (communityEntity.getAuthor() == null || !communityEntity.getAuthor().getId().equals(communityRequestDTO.getUserId())) {
            throw new IllegalStateException("Only the author can update this community");
        }

        // 게시글 업데이트
        communityEntity.setTitle(communityRequestDTO.getTitle());
        communityEntity.setContent(communityRequestDTO.getContent());
        communityEntity.setType(communityRequestDTO.getType());

        // 파일 그룹 처리
        FileGroupEntity fileGroup = communityEntity.getFileGroupEntity();
        if (fileGroup == null) {
            fileGroup = new FileGroupEntity();
            communityEntity.setFileGroupEntity(fileGroup);
        }

        // 기존 이미지 삭제 로직
        if (fileGroup.getImages() != null && !fileGroup.getImages().isEmpty()) {
            List<ImageEntity> existingImages = new ArrayList<>(fileGroup.getImages());
            for (ImageEntity image : existingImages) {
                String localPath = "/Roomy-Backend/uploads/" + image.getImageUrl().substring("/Roomy-Backend/uploads/".length());
                try {
                    boolean isFileDeleted = Files.deleteIfExists(Paths.get(localPath));
                    if (isFileDeleted) {
                        System.out.println("파일 삭제 성공: " + localPath);
                    } else {
                        System.out.println("파일 삭제 실패 또는 존재하지 않음: " + localPath);
                    }
                } catch (IOException e) {
                    System.err.println("파일 삭제 중 오류 발생: " + e.getMessage());
                }
                imageRepository.delete(image);
            }
            fileGroup.getImages().clear();
        }

        // 새로운 이미지 추가
        if (communityRequestDTO.getImages() != null && !communityRequestDTO.getImages().isEmpty()) {
            for (MultipartFile file : communityRequestDTO.getImages()) {
                try {
                    String filePath = fileService.saveAndResizeImage(file, 800, 600); // 해상도 조정 포함 저장
                    ImageEntity imageEntity = ImageEntity.builder()
                            .imageUrl(filePath)
                            .fileGroup(fileGroup)
                            .build();
                    fileGroup.getImages().add(imageEntity);
                    System.out.println("새로운 이미지 저장 성공: " + filePath);
                } catch (IOException e) {
                    System.err.println("새로운 이미지 저장 실패: " + file.getOriginalFilename() + ", 오류: " + e.getMessage());
                }
            }
        } else {
            System.out.println("새로운 이미지가 제공되지 않았습니다.");
        }

        // 엔티티 저장 및 응답 반환
        CommunityEntity savedEntity = communityRepository.save(communityEntity);
        System.out.println("커뮤니티 수정 성공: communityId=" + savedEntity.getCommunityId());
        return toResponseDTO(savedEntity);
    }



    @Override
    public CommunityResponseDTO getCommunity(Long id) {
        CommunityEntity communityEntity = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));
        return toResponseDTO(communityEntity);
    }

    @Override
    public List<CommunityResponseDTO> getAllCommunities() {
        return communityRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String deleteCommunity(Long id, Long userId) {
        // 게시글 존재 여부 확인
        CommunityEntity communityEntity = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Community not found with id: " + id));

        // 작성자 검증
        if (communityEntity.getAuthor() == null || !communityEntity.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException("Only the author can delete this community");
        }

        // 파일 삭제 로직
        FileGroupEntity fileGroup = communityEntity.getFileGroupEntity();
        if (fileGroup != null && fileGroup.getImages() != null && !fileGroup.getImages().isEmpty()) {
            for (ImageEntity image : fileGroup.getImages()) {
                String localPath = "/Roomy-Backend/uploads/" + image.getImageUrl().substring("/Roomy-Backend/uploads/".length());
                try {
                    boolean isFileDeleted = Files.deleteIfExists(Paths.get(localPath));
                    if (isFileDeleted) {
                        System.out.println("파일 삭제 성공: " + localPath);
                    } else {
                        System.out.println("파일 삭제 실패 또는 존재하지 않음: " + localPath);
                    }
                } catch (IOException e) {
                    System.err.println("파일 삭제 중 오류 발생: " + localPath + " - 오류 메시지: " + e.getMessage());
                }
            }
        }

        // 게시글 삭제
        communityRepository.delete(communityEntity);
        System.out.println("게시글 삭제 성공: communityId=" + id);

        // 삭제 결과 메시지 반환
        return "게시글이 성공적으로 삭제되었습니다. 게시글 ID: " + id;
    }



    @Override
    @Transactional
    public void increaseViewCount(Long id){
        communityRepository.increaseViewCount(id);
        System.out.println("조회수가 증가 한 게시글 아이디 : " + id);
    }

    private CommunityResponseDTO toResponseDTO(CommunityEntity communityEntity) {
        List<String> imageUrls = communityEntity.getFileGroupEntity().getImages().stream()
                .map(ImageEntity::getImageUrl)
                .collect(Collectors.toList());

        return CommunityResponseDTO.builder()
                .communityId(communityEntity.getCommunityId())
                .title(communityEntity.getTitle())
                .content(communityEntity.getContent())
                .author(communityEntity.getAuthor().getUsername())
                .type(communityEntity.getType())
                .likeCount(communityEntity.getLikes() != null ? communityEntity.getLikes().size() : 0)
                .createdAt(communityEntity.getCreatedAt())
                .updatedAt(communityEntity.getUpdatedAt())
                .views(communityEntity.getViews())
                .imageUrls(imageUrls)
                .build();
    }


}
