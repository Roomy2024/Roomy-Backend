package com.example.Roomy.SocialLogin.DTO;

import lombok.Data;

@Data
public class UserRequest {
    private Long id;
    private String username;
    private int age;
    private String area;
    private String gender;
    private String profile;
}