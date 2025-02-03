package com.example.Roomy.like.entity;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.comment.entity.CommentEntity;
import com.example.Roomy.comment.entity.ReplyEntity;
import com.example.Roomy.community.entity.CommunityEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "likes")
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = true)
    @Schema(description = "커뮤니티")
    private CommunityEntity community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = true)
    @Schema(description = "댓글")
    private CommentEntity comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id", nullable = true)
    @Schema(description = "대댓글")
    private ReplyEntity reply;

    // 엔티티 저장 전 유효성 검사
    @PrePersist
    @PreUpdate
    private void validateLikeEntity() {
        int nonNullCount = 0;

        if (community != null) nonNullCount++;
        if (comment != null) nonNullCount++;
        if (reply != null) nonNullCount++;

        // community, comment, reply 중 정확히 하나만 설정되어야 합니다.
        if (nonNullCount != 1) {
            throw new IllegalStateException("3개중 하나만 설정되어야 함");
        }
    }
}



