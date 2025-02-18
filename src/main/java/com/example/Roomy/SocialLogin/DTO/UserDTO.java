package com.example.Roomy.SocialLogin.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserDTO {
    private String username;
    private String email;
    private String area;
    private String gender;
    //private int age;
    private MultipartFile profile;

    public UserDTO(String username, String email, String area, String gender, MultipartFile profile){
        this.username=username;
        this.email=email;
        this.area=area;
        this.gender=gender;
        //this.age=age;
        this.profile=profile;
    }
}
