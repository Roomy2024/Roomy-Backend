package com.example.Roomy.community.dto;

import com.example.Roomy.image.entity.FileGroupEntity;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CommunityDTO {
    private Long id;
    private String title;
    private String content;
    private FileGroupEntity images;
    private Long authorId;

    public CommunityDTO(Long id, String title, String content, FileGroupEntity images, Long authorId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.images =images;
        this.authorId = authorId;
    }
}
