package com.example.Roomy.community.entity;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.image.entity.FileGroupEntity;
import com.example.Roomy.like.entity.LikeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("작성자")
    private User author;

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

    @Column(nullable = false)
    @Builder.Default
    private int views = 0;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LikeEntity> likes = new ArrayList<>();
}
