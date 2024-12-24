package com.example.Roomy.community.service;

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
    private  final ImageRepository imageRepository;

    @Override
    @Transactional
    public CommunityResponseDTO createCommunity(CommunityRequestDTO communityRequestDTO) throws IOException {
        CommunityEntity communityEntity = CommunityEntity.builder()
                .title(communityRequestDTO.getTitle())
                .content(communityRequestDTO.getContent())
                .type(communityRequestDTO.getType())
                .build();

        FileGroupEntity fileGroup = new FileGroupEntity();
        if (communityRequestDTO.getImages() != null && !communityRequestDTO.getImages().isEmpty()) {
            for (MultipartFile file : communityRequestDTO.getImages()) {
                String filePath = fileService.saveFile(file);
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

        // 커뮤니티 데이터 업데이트
        communityEntity.setTitle(communityRequestDTO.getTitle());
        communityEntity.setContent(communityRequestDTO.getContent());
        communityEntity.setType(communityRequestDTO.getType());

        FileGroupEntity fileGroup = communityEntity.getFileGroupEntity();
        if (fileGroup == null) {
            fileGroup = new FileGroupEntity();
            communityEntity.setFileGroupEntity(fileGroup);
        }

        // 기존 이미지 삭제 로직
        if (fileGroup.getImages() != null && !fileGroup.getImages().isEmpty()) {
            List<ImageEntity> existingImages = new ArrayList<>(fileGroup.getImages());
            for (ImageEntity image : existingImages) {
                String localPath = "C:/uploads/" + image.getImageUrl().substring("/uploads/".length());

                // 로컬 디스크 파일 삭제
                boolean isFileDeleted = Files.deleteIfExists(Paths.get(localPath));
                if (isFileDeleted) {
                    System.out.println("파일 삭제 성공: " + localPath);
                } else {
                    System.out.println("파일 삭제 실패 또는 존재하지 않음: " + localPath);
                }

                // 데이터베이스에서 이미지 삭제
                imageRepository.delete(image);
                System.out.println("이미지 삭제 성공 (DB): imageId=" + image.getImageId());
            }
            fileGroup.getImages().clear();
        }

        // 새로운 이미지 추가
        if (communityRequestDTO.getImages() != null && !communityRequestDTO.getImages().isEmpty()) {
            for (MultipartFile file : communityRequestDTO.getImages()) {
                String filePath = fileService.saveFile(file);
                ImageEntity imageEntity = ImageEntity.builder()
                        .imageUrl(filePath)
                        .fileGroup(fileGroup)
                        .build();
                fileGroup.getImages().add(imageEntity);
            }
        }

        // 엔티티 저장 및 응답 반환
        communityRepository.save(communityEntity);
        return toResponseDTO(communityEntity);
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
    public String deleteCommunity(Long id) {
        // 게시글이 존재하는지 확인
        CommunityEntity communityEntity = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Community not found with id: " + id));

        // 파일 삭제 로직
        FileGroupEntity fileGroup = communityEntity.getFileGroupEntity();
        if (fileGroup != null && fileGroup.getImages() != null) {
            for (ImageEntity image : fileGroup.getImages()) {
                try {
                    String localPath = "C:/uploads/" + image.getImageUrl().substring("/uploads/".length());
                    boolean isFileDeleted = Files.deleteIfExists(Paths.get(localPath));
                    if (isFileDeleted) {
                        System.out.println("파일 삭제 성공: " + localPath);
                    } else {
                        System.out.println("파일 삭제 실패 또는 파일이 존재하지 않음: " + localPath);
                    }
                } catch (IOException e) {
                    System.err.println("파일 삭제 중 오류 발생: " + e.getMessage());
                }
            }
        }

        // 게시글 삭제
        communityRepository.delete(communityEntity);
        System.out.println("게시글 삭제 성공: communityId=" + id);

        // 삭제 결과 메시지 반환
        return "게시글이 성공적으로 삭제되었습니다. 게시글 ID: " + id;
    }


    private CommunityResponseDTO toResponseDTO(CommunityEntity communityEntity) {
        List<String> imageUrls = communityEntity.getFileGroupEntity().getImages().stream()
                .map(ImageEntity::getImageUrl)
                .collect(Collectors.toList());

        return CommunityResponseDTO.builder()
                .communityId(communityEntity.getCommunityId())
                .title(communityEntity.getTitle())
                .content(communityEntity.getContent())
                .type(communityEntity.getType())
                .createdAt(communityEntity.getCreatedAt())
                .updatedAt(communityEntity.getUpdatedAt())
                .imageUrls(imageUrls)
                .build();
    }
}
