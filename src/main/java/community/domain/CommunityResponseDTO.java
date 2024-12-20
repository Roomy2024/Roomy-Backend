package community.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityResponseDTO {

    private Long communityId;      // 커뮤니티 ID
    private String title;          // 제목
    private String content;        // 내용
    private String type;           // 타입
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간

    // 작성자 관련 필드 추가 (유저 엔티티 생성 시 사용)
    // private String author; // 작성자 이름
}
