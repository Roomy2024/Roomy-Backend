package com.example.Roomy.SocialLogin.Controller;

import com.example.Roomy.SocialLogin.DTO.UserRequest;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    //추가 정보 저장
    @PostMapping("/update-userinfo")
    public ResponseEntity<User> upsateUserInfo(@RequestBody UserRequest userRequest){
        User updateUser = userService.updateUserInfo(
                userRequest.getEmail(),
                userRequest.getUsername(),
                userRequest.getAge(),
                userRequest.getArea(),
                userRequest.getGender(),
                userRequest.getProfile());

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(updateUser.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(updateUser.getId());

        // Refresh Token 저장
        userService.saveRefreshToken(updateUser, refreshToken);

        // 응답 헤더에 Access Token과 Refresh Token 추가
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .header("Refresh-Token", refreshToken)
                .body(updateUser);
    }
}
