package com.example.Roomy.SocialLogin.Dto.User;

import lombok.Data;

@Data
public class UserResponse {
    private String email;
    private String username;
    private String profile;
    private int age;
    private String gender;
    private String area;
}