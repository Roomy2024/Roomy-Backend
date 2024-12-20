package community.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "community")
public class CommunityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("게시판 번호")
    private Long communityId;

    // 작성자 필드 추가 (유저 엔티티 생성 시 사용)
    // @Comment("작성자")
    // @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // @OnDelete(action = OnDeleteAction.CASCADE)
    // private User author;

    @Column(nullable = false)
    @Comment("제목")
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("내용")
    private String content;

    @Column(nullable = false)
    @Comment("커뮤니티 타입")
    private String type;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Comment("작성 시간")
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    // DTO 변환 메서드 (작성자 관련 코드 주석 처리)
    public CommunityResponseDTO toResponseDTO() {
        return CommunityResponseDTO.builder()
                .communityId(this.communityId)
                .title(this.title)
                .content(this.content)
                .type(this.type)
                // .author(this.author != null ? this.author.getUsername() : null) // 작성자 이름 추가 가능
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    // 업데이트 메서드
    public void update(String title, String content, String type) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.updatedAt = LocalDateTime.now();
    }
}
