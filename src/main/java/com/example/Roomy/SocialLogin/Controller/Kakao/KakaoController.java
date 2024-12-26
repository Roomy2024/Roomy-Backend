package com.example.Roomy.SocialLogin.Controller.Kakao;

import com.example.Roomy.SocialLogin.Entity.Response.UserResponse;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Service.Kakao.KakaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    public KakaoController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @GetMapping("/email")
    //카카오 로그인 (이메일 요청)
    public ResponseEntity<String> getEmailFromKakao(@RequestParam("authorizationCode") String authorizationCode) {
        // 이메일 정보 가져오기
        String email = kakaoService.getEmailFromKakao(authorizationCode);

        return ResponseEntity.ok("Email retrieved successfully: " + email);
    }

    //사용자 정보저장, JWT 발급
    @PostMapping("/save")
    public ResponseEntity<String> saveUserInfo(@RequestBody UserResponse userResponse) {
        // 이메일과 추가 정보를 한 번에 저장
        String jwtToken = kakaoService.saveUserInfo(userResponse.getEmail(), userResponse);

        return ResponseEntity.ok("User information saved successfully. JWT Token: " + jwtToken);
    }


    //Authorization Code 처리
    @GetMapping("/code")
    public ResponseEntity<String> handleKakaoRedirect(@RequestParam("code") String code) {
        // Authorization Code를 로그로 출력
        System.out.println("Authorization Code: " + code);

        // 필요한 로직 추가 (예: Access Token 요청)
        return ResponseEntity.ok("Authorization Code received: " + code);
    }
}
