package com.example.Roomy.comment.entity;

import com.example.Roomy.SocialLogin.Model.User;
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
@Table(name = "replies")
public class ReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("대댓글 번호")
    private Long replyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    @Comment("부모 댓글 번호")
    private CommentEntity parentComment;

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

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("대댓글 내용")
    private String content;

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    @Builder.Default
    private List<LikeEntity> likes = new ArrayList<>();
}
