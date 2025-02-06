package com.example.Roomy.chatReport.Entity;

import com.example.Roomy.comment.entity.CommentEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
public class ChatReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OneToOneChat_id", nullable = true) // 댓글 ID 저장
    private CommentEntity OneToOneChat;

    @ManyToOne
    @JoinColumn(name = "groupChat_id", nullable = true) // 댓글 ID 저장
    private CommentEntity groupChat;

    //신고 시간
    private LocalDateTime localDateTime;
}
