package com.example.Roomy.community.service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.community.dto.CommunityRequestDTO;
import com.example.Roomy.community.dto.CommunityResponseDTO;
import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.community.repository.CommunityRepository;
import com.example.Roomy.image.entity.FileGroupEntity;
import com.example.Roomy.image.entity.ImageEntity;
import com.example.Roomy.image.repository.ImageRepository;
import com.example.Roomy.image.service.FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    private static final Logger logger = LoggerFactory.getLogger(CommunityServiceImpl.class);

    @Override
    @Transactional
    public CommunityResponseDTO createCommunity(CommunityRequestDTO communityRequestDTO) throws IOException {
        User author = userRepository.findById(communityRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CommunityEntity communityEntity = CommunityEntity.builder()
                .title(communityRequestDTO.getTitle())
                .content(communityRequestDTO.getContent())
                .type(communityRequestDTO.getType())
                .author(author)
                .build();

        FileGroupEntity fileGroup = new FileGroupEntity();
        if (communityRequestDTO.getImages() != null && !communityRequestDTO.getImages().isEmpty()) {
            for (MultipartFile file : communityRequestDTO.getImages()) {
                String fileUrl = fileService.uploadImageToS3(file, 800, 600);
                ImageEntity imageEntity = ImageEntity.builder()
                        .imageUrl(fileUrl)
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

        if (communityEntity.getAuthor() == null || !communityEntity.getAuthor().getId().equals(communityRequestDTO.getUserId())) {
            throw new IllegalStateException("Only the author can update this community");
        }

        communityEntity.setTitle(communityRequestDTO.getTitle());
        communityEntity.setContent(communityRequestDTO.getContent());
        communityEntity.setType(communityRequestDTO.getType());

        FileGroupEntity fileGroup = communityEntity.getFileGroupEntity();
        if (fileGroup == null) {
            fileGroup = new FileGroupEntity();
            communityEntity.setFileGroupEntity(fileGroup);
        }

        if (fileGroup.getImages() != null && !fileGroup.getImages().isEmpty()) {
            List<ImageEntity> existingImages = new ArrayList<>(fileGroup.getImages());
            for (ImageEntity image : existingImages) {
                try {
                    fileService.deleteFileFromS3(image.getImageUrl());
                } catch (Exception e) {
                    logger.error("파일 삭제 중 오류 발생: {}", image.getImageUrl(), e);
                }
                imageRepository.delete(image);
            }
            fileGroup.getImages().clear();
        }

        if (communityRequestDTO.getImages() != null && !communityRequestDTO.getImages().isEmpty()) {
            for (MultipartFile file : communityRequestDTO.getImages()) {
                String filePath = fileService.uploadImageToS3(file, 800, 600);
                ImageEntity imageEntity = ImageEntity.builder()
                        .imageUrl(filePath)
                        .fileGroup(fileGroup)
                        .build();
                fileGroup.getImages().add(imageEntity);
            }
        }

        CommunityEntity savedEntity = communityRepository.save(communityEntity);
        return toResponseDTO(savedEntity);
    }

    @Override
    public CommunityResponseDTO getCommunity(Long id) {
        CommunityEntity communityEntity = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));
        return toResponseDTO(communityEntity);
    }

    @Override
    public List<CommunityResponseDTO> getMyCommunities(Long userId) {
        List<CommunityEntity> userCommunities = communityRepository.findByAuthorId(userId);
        return userCommunities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
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

        // 파일 삭제 로직 (S3에 업로드된 파일 삭제)
        FileGroupEntity fileGroup = communityEntity.getFileGroupEntity();
        if (fileGroup != null && fileGroup.getImages() != null && !fileGroup.getImages().isEmpty()) {
            for (ImageEntity image : fileGroup.getImages()) {
                try {
                    fileService.deleteFileFromS3(image.getImageUrl());
                } catch (Exception e) {
                    logger.error("파일 삭제 중 오류 발생: {}", image.getImageUrl(), e);
                }
            }
        }

        // 게시글 삭제
        communityRepository.delete(communityEntity);
        logger.info("게시글 삭제 성공: communityId={}", id);

        // 삭제 결과 메시지 반환
        return "게시글이 성공적으로 삭제되었습니다. 게시글 ID: " + id;
    }

    @Override
    @Transactional
    public void increaseViewCount(Long id) {
        communityRepository.increaseViewCount(id);
        logger.info("조회수가 증가 한 게시글 아이디: {}", id);
    }

    private CommunityResponseDTO toResponseDTO(CommunityEntity communityEntity) {
        List<String> imageUrls = new ArrayList<>();
        if (communityEntity.getFileGroupEntity() != null && communityEntity.getFileGroupEntity().getImages() != null) {
            imageUrls = communityEntity.getFileGroupEntity().getImages().stream()
                    .map(ImageEntity::getImageUrl)
                    .collect(Collectors.toList());
        }

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
