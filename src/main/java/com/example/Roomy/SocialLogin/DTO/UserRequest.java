package com.example.Roomy.SocialLogin.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequest {
    private Long id;
    private String username;
    //private int age;
    private String area;
    private String gender;
    private MultipartFile profileImage;
}