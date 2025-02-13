package com.example.Roomy.SocialLogin.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String email;
    private String area;
    private String gender;
    private int age;
    private String profile;

    public UserDTO(String username, String email, String area, String gender, int age, String profile){
        this.username=username;
        this.email=email;
        this.area=area;
        this.gender=gender;
        this.age=age;
        this.profile=profile;
    }
}
