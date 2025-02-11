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
@Builder // ✅ builder()를 사용하기 위해 필요
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    private String message;

    private LocalDateTime createdAt;
}
