package com.example.Roomy.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
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

}
