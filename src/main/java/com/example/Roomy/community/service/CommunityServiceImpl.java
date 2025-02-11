package com.example.Roomy.community.service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.bookmark.repository.BookmarkRepository;
import com.example.Roomy.comment.entity.CommentEntity;
import com.example.Roomy.comment.repository.CommentRepository;
import com.example.Roomy.comment.repository.ReplyRepository;
import com.example.Roomy.community.dto.CommunityRequestDTO;
import com.example.Roomy.community.dto.CommunityResponseDTO;
import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.community.repository.CommunityRepository;
import com.example.Roomy.image.entity.FileGroupEntity;
import com.example.Roomy.image.entity.ImageEntity;
import com.example.Roomy.image.repository.ImageRepository;
import com.example.Roomy.image.service.FileService;
import com.example.Roomy.like.repository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
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
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;

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
                String fileUrl = fileService.uploadCommunityImageToS3(file);
                ImageEntity imageEntity = ImageEntity.builder()
                        .imageUrl(fileUrl)
                        .fileGroup(fileGroup)
                        .build();
                fileGroup.getImages().add(imageEntity);
            }
        }
        communityEntity.setFileGroupEntity(fileGroup);
        communityRepository.save(communityEntity);
        return toResponseDTO(communityEntity, 0); // 댓글 수는 0으로 초기화
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
                String filePath = fileService.uploadCommunityImageToS3(file);
                ImageEntity imageEntity = ImageEntity.builder()
                        .imageUrl(filePath)
                        .fileGroup(fileGroup)
                        .build();
                fileGroup.getImages().add(imageEntity);
            }
        }

        CommunityEntity savedEntity = communityRepository.save(communityEntity);
        int totalCommentCount = getTotalCommentCount(id);
        return toResponseDTO(savedEntity, totalCommentCount);
    }

    @Override
    public CommunityResponseDTO getCommunity(Long id) {
        CommunityEntity communityEntity = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));
        int totalCommentCount = getTotalCommentCount(id);
        return toResponseDTO(communityEntity, totalCommentCount);
    }

    @Override
    public List<CommunityResponseDTO> getMyCommunities(Long userId) {
        List<CommunityEntity> userCommunities = communityRepository.findByAuthorId(userId);
        return userCommunities.stream()
                .map(community -> {
                    int totalCommentCount = getTotalCommentCount(community.getCommunityId());
                    return toResponseDTO(community, totalCommentCount);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<CommunityResponseDTO> getAllCommunities(Pageable pageable) {
        Page<CommunityEntity> communityPage = communityRepository.findAll(pageable);

        List<CommunityResponseDTO> communityResponseDTOList = communityPage.getContent().stream()
                .map(community -> toResponseDTO(community, getTotalCommentCount(community.getCommunityId()))) // ✅ 람다식 사용
                .collect(Collectors.toList());

        return new PageImpl<>(communityResponseDTOList, pageable, communityPage.getTotalElements());
    }



    @Override
    @Transactional
    public String deleteCommunity(Long id, Long userId) {
        CommunityEntity communityEntity = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Community not found with id: " + id));

        if (!communityEntity.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException("Only the author can delete this community");
        }

        likeRepository.deleteAllByCommunity(communityEntity);
        bookmarkRepository.deleteByCommunity_CommunityId(id);

        List<CommentEntity> comments = commentRepository.findByCommunity_CommunityId(id);
        for (CommentEntity comment : comments) {
            replyRepository.deleteAll(replyRepository.findByParentComment_CommentId(comment.getCommentId()));
        }
        commentRepository.deleteAll(comments);

        communityRepository.delete(communityEntity);
        logger.info("Community deleted successfully: communityId={}", id);
        return "Community successfully deleted. ID: " + id;
    }

    @Override
    @Transactional
    public void increaseViewCount(Long id) {
        communityRepository.increaseViewCount(id);
        logger.info("조회수가 증가 한 게시글 아이디: {}", id);
    }

    @Override
    public int getTotalCommentCount(Long communityId) {
        int commentCount = commentRepository.countByCommunityId(communityId);
        int replyCount = replyRepository.countRepliesByCommunityId(communityId);
        return commentCount + replyCount;
    }

    private CommunityResponseDTO toResponseDTO(CommunityEntity communityEntity, int totalCommentCount) {
        ZoneId kstZone = ZoneId.of("Asia/Seoul");

        return CommunityResponseDTO.builder()
                .communityId(communityEntity.getCommunityId())
                .title(communityEntity.getTitle())
                .content(communityEntity.getContent())
                .author(communityEntity.getAuthor().getUsername())
                .type(communityEntity.getType())
                .likeCount(communityEntity.getLikes() != null ? communityEntity.getLikes().size() : 0)
                .totalCommentCount(totalCommentCount)
                .createdAt(communityEntity.getCreatedAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(kstZone).toLocalDateTime())
                .updatedAt(communityEntity.getUpdatedAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(kstZone).toLocalDateTime())
                .views(communityEntity.getViews())
                .imageUrls(communityEntity.getFileGroupEntity().getImages().stream()
                        .map(ImageEntity::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}
