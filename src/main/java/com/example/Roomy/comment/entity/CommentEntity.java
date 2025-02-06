package com.example.Roomy.comment.entity;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.like.entity.LikeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name = "comments")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("댓글 번호")
    @Schema(description = "댓글번호 ID")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    @Comment("커뮤니티 글 번호")
    @Schema(description = "커뮤니티 글 번호")
    private CommunityEntity community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("작성자")
    @Schema(description = "작성자/UserId")
    private User author;

    @CreationTimestamp
    @Comment("작성 시간")
    @Schema(description = "작성 시간")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Comment("수정 시간")
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("댓글 내용")
    @Schema(description = "댓글 내용")
    private String content;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "대댓글")
    private List<ReplyEntity> replies = new ArrayList<>();
}
