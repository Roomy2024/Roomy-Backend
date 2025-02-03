package com.example.Roomy.SocialLogin.DTO;

import lombok.Data;

@Data
public class UserRequest {
    private String email;
    private String username;
    private int age;
    private String area;
    private String gender;
    private String profile;
}