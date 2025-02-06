package com.example.Roomy.Report.Entity;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.comment.entity.CommentEntity;
import com.example.Roomy.community.entity.CommunityEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //신고 타입 ex)채팅, 개시글, 댓글
    private String type;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    //신고 한 사람
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "reported_id", nullable = false)
    //신고 당한 사람
    private User reported;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = true) // 게시글 ID 저장
    private CommunityEntity community;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = true) // 댓글 ID 저장
    private CommentEntity comment;

    //신고 시간
    private LocalDateTime localDateTime;

}
