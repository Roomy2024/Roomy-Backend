package com.example.Roomy.bookmark.dto;

import lombok.Data;

@Data
public class BookmarkDTO {

    private Long id;
    private Long userId;
    private Long communityId;
    private boolean active;
}
