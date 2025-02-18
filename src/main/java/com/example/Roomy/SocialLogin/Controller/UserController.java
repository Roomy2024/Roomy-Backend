package com.example.Roomy.SocialLogin.Controller;

import com.example.Roomy.SocialLogin.DTO.UserRequest;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.SocialLogin.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(value = "/update-userinfo")
    public ResponseEntity<String> updateUserInfo(@ModelAttribute UserRequest userRequest, @RequestHeader("Authorization") String token , HttpServletRequest Request) throws IOException {
        System.out.println(userRequest);
        System.out.println(Request.getHeader("Authorization"));

        String jwtToken = token.replace("Bearer ", "");
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(jwtToken));

        userService.updateUserInfo(userRequest, userId);

        return ResponseEntity.ok().body("회원가입 성공");
    }
}