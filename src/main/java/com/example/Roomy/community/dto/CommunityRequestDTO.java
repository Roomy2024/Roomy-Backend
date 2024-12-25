package com.example.Roomy.community.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityRequestDTO {
    private String title;
    private String content;
    private String type;
    private List<MultipartFile> images;
}
