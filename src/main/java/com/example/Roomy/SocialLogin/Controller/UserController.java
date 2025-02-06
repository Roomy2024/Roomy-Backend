package com.example.Roomy.SocialLogin.Controller;

import com.example.Roomy.SocialLogin.DTO.UserRequest;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    //추가 정보 저장
    @PostMapping("/update-userinfo")
    public ResponseEntity<User> upsateUserInfo(@RequestBody UserRequest userRequest, @RequestHeader("Authorization") String token){
        String jwtToken = token.replace("Bearer ", ""); // "Bearer " 제거
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(jwtToken));

        User updateUser = userService.updateUserInfo(
                userId,
                userRequest.getUsername(),
                userRequest.getAge(),
                userRequest.getArea(),
                userRequest.getGender(),
                userRequest.getProfile());

        return ResponseEntity.ok().body(updateUser);
    }
}
