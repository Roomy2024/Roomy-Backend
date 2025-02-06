package com.example.Roomy.SocialLogin.Controller;

import com.example.Roomy.SocialLogin.DTO.UserRequest;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.SocialLogin.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    //추가 정보 저장
    @PostMapping("/update-userinfo")
    public ResponseEntity<User> upsateUserInfo(@RequestBody UserRequest userRequest, @RequestParam("id") Long id){
        // ID로 사용자 조회
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 ID의 사용자를 찾을 수 없습니다."));

        User updateUser = userService.updateUserInfo(
                id,
                userRequest.getUsername(),
                userRequest.getAge(),
                userRequest.getArea(),
                userRequest.getGender(),
                userRequest.getProfile());

        return ResponseEntity.ok().body(updateUser);
    }
}
