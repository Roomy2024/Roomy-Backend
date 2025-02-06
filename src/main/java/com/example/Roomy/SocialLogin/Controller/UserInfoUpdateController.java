package com.example.Roomy.SocialLogin.Controller;

import com.example.Roomy.SocialLogin.DTO.UserAreaUpdateRequest;
import com.example.Roomy.SocialLogin.DTO.UserNameUpdateRequest;
import com.example.Roomy.SocialLogin.DTO.UserRequest;
import com.example.Roomy.SocialLogin.DTO.UserProfileUpdateRequest;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/update")
public class UserInfoUpdateController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/profile")
    public ResponseEntity<String> updateUserProfile(@RequestBody UserProfileUpdateRequest userProfileUpdateRequest, @RequestHeader("Authorization") String token){
        String jwtToken = token.replace("Bearer ", ""); // "Bearer " 제거
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(jwtToken));

        User updateUser = userService.updateUserprofile(
                userId,
                userProfileUpdateRequest.getProfile());
        return ResponseEntity.ok().body("프로필 변경이 완료되었습니다");
    }
    @PostMapping("/username")
    public ResponseEntity<String> upsdateUserName(@RequestBody UserNameUpdateRequest userNameUpdateRequest, @RequestHeader("Authorization") String token){
        String jwtToken = token.replace("Bearer ", ""); // "Bearer " 제거
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(jwtToken));

        User updateUser = userService.updateUserName(
                userId,
                userNameUpdateRequest.getUsername());
        return ResponseEntity.ok().body("닉네임 변경이 완료되었습니다.");
    }
    @PostMapping("/area")
    public ResponseEntity<String> updateUserArea(@RequestBody UserAreaUpdateRequest userAreaUpdateRequest, @RequestHeader("Authorization") String token){
        String jwtToken = token.replace("Bearer ", ""); // "Bearer " 제거
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(jwtToken));

        User updateUser = userService.updateUserArea(
                userId,
                userAreaUpdateRequest.getArea());
        return ResponseEntity.ok().body("지역 변경이 완료되었습니다.");
    }
}
