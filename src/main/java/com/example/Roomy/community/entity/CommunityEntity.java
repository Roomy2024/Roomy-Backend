package com.example.Roomy.community.entity;

import com.example.Roomy.image.entity.FileGroupEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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

    @CreationTimestamp
    @Comment("작성 시간")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Comment("수정 시간")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Comment("제목")
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("내용")
    private String content;

    @Column(nullable = false)
    @Comment("커뮤니티 타입")
    private String type;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "file_group_id")
    private FileGroupEntity fileGroupEntity;
}
