package com.example.Roomy.SocialLogin.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class User {

    //DB 연결 후 수정
    //@ID
    //GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String socialType;

    @Column(nullable = false)
    private String username;

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
}
