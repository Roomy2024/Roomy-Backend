package com.example.Roomy.notification.entity;

import com.example.Roomy.SocialLogin.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 수신자 (알림을 받는 유저)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 발신자 (알림을 보낸 유저)
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // 알림 메시지 내용
    private String message;

    // 알림 생성 시간
    private LocalDateTime createdAt;
}
