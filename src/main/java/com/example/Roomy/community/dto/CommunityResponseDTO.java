package com.example.Roomy.community.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityResponseDTO {
    private Long communityId;
    private String title;
    private String content;
    private String type;
    private int views;
    private String author;
    private int likeCount;
    private int totalCommentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> imageUrls;
}
