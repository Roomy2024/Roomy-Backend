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

    @PostMapping(value = "/update-userinfo")
    public ResponseEntity<User> updateUserInfo(@ModelAttribute UserRequest userRequest) throws IOException {
        User updatedUser = userService.updateUserInfo(userRequest);
        return ResponseEntity.ok().body(updatedUser);
    }
}
