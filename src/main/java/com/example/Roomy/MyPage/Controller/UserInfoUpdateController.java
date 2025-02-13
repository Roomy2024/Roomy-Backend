package com.example.Roomy.MyPage.Controller;

import com.example.Roomy.MyPage.Service.UserInfoUpdateService;
import com.example.Roomy.SocialLogin.DTO.UserAreaUpdateRequest;
import com.example.Roomy.SocialLogin.DTO.UserNameUpdateRequest;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage/update")
public class UserInfoUpdateController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserInfoUpdateService userInfoUpdateService;

    @PostMapping("/profile")
    public ResponseEntity<String> updateUserProfile(
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestHeader("Authorization") String token) throws IOException {

        String jwtToken = token.replace("Bearer ", "");
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(jwtToken));

        User updatedUser = userInfoUpdateService.updateUserProfile(userId, profileImage);

        return ResponseEntity.ok().body("프로필 이미지 변경이 완료되었습니다: " + updatedUser.getProfile());
    }

    @PostMapping("/username")
    public ResponseEntity<String> updateUserName(@RequestBody UserNameUpdateRequest userNameUpdateRequest, @RequestHeader("Authorization") String token){
        String jwtToken = token.replace("Bearer ", ""); // "Bearer " 제거
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(jwtToken));

        User updateUser = userInfoUpdateService.updateUserName(
                userId,
                userNameUpdateRequest.getUsername());
        return ResponseEntity.ok().body("닉네임 변경이 완료되었습니다.");
    }
    @PostMapping("/area")
    public ResponseEntity<String> updateUserArea(@RequestBody UserAreaUpdateRequest userAreaUpdateRequest, @RequestHeader("Authorization") String token){
        String jwtToken = token.replace("Bearer ", ""); // "Bearer " 제거
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(jwtToken));

        User updateUser = userInfoUpdateService.updateUserArea(
                userId,
                userAreaUpdateRequest.getArea());
        return ResponseEntity.ok().body("지역 변경이 완료되었습니다.");
    }
}
