package com.example.Roomy.SocialLogin.Controller;

import com.example.Roomy.SocialLogin.DTO.UserRequest;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.SocialLogin.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    // 멀티파트폼으로 해야함
    //추가 정보 저장
    @PostMapping(value = "/update-userinfo", consumes = {"multipart/form-data"})
    public ResponseEntity<User> updateUserInfo(
            @RequestParam("id") Long id,
            @RequestParam("username") String username,
            @RequestParam("age") int age,
            @RequestParam("area") String area,
            @RequestParam("gender") String gender,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        User updateUser = userService.updateUserInfo(id, username, age, area, gender, profileImage);
        return ResponseEntity.ok().body(updateUser);
    }
}
