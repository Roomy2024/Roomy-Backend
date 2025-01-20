package com.example.Roomy.SocialLogin.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class User {

    //DB 연결 후 수정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String socialType;

    @Column(nullable = true)
    private String username ;

    @Column
    private String email;

    @Column
    private String profile;

    @Column
    private int age;

    @Column
    private String gender;

    @Column
    private String area;

    @Column
    private boolean emailAgree;

    //영속성 전이설정, 고아 객체 제거 설정
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    //user 테이블에서 생성될 외래 키 열 이름, 참조하는 refreshtoken 열 이름
    @JoinColumn(name = "refresh_token_id", referencedColumnName = "id")
    private RefreshToken refreshToken;
}
