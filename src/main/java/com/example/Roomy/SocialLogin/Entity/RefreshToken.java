package com.example.Roomy.SocialLogin.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String refreshToken;

    //user와 양방향 관계 설정
    //관계 주인 설정, 영속성 전이 설정
    @OneToOne(mappedBy = "refreshToken", cascade = CascadeType.ALL)
    private User user;
}
