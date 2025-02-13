package com.example.Roomy.MyPage.Controller;

import com.example.Roomy.MyPage.Service.MyPageService;
import com.example.Roomy.SocialLogin.DTO.UserDTO;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MyPageService myPageService;

    @GetMapping("/mypage")
    public ResponseEntity<?> mypage(@RequestHeader("Authorization") String token){

        String jwtToken = token.replace("Bearer ", "");
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(jwtToken));

        UserDTO user = myPageService.ShowMyPage(userId);

        return ResponseEntity.ok().body(user);
    }
}
