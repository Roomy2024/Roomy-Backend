package com.example.Roomy.report.entity;


import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.community.entity.CommunityEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reports")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // 신고자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_community_id")
    private CommunityEntity reportedCommunity; // 신고게시글

    @Column(nullable = false)
    private String reason; // 사유

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status; // 상태처리상태

    @Column(nullable = false, updatable  = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = ReportStatus.PENDING; // 기본상태 대기
    }
}
