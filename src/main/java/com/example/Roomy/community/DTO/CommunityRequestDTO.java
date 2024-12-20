package com.example.Roomy.community.DTO;

import com.example.Roomy.community.entity.CommunityEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityRequestDTO {

    private String title;    // 제목
    private String content;  // 내용
    private String type;     // 커뮤니티 타입

    // 작성자 관련 필드 추가 (유저 엔티티 생성 시 사용)
    // private Long authorId;

    // CommunityEntity로 변환
    public CommunityEntity toEntity() {
        return CommunityEntity.builder()
                .title(this.title)
                .content(this.content)
                .type(this.type)
                // .author(new User(this.authorId)) // 작성자 ID를 유저 엔티티로 변환
                .build();
    }
}
