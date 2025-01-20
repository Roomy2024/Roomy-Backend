package com.example.Roomy.SocialLogin.Controller.Kakao;

import com.example.Roomy.SocialLogin.Config.KakaoConfig;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Service.Kakao.KakaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth/kakao")
public class OAuthController {

    private final KakaoService kakaoService;
    private final KakaoConfig kakaoConfig;

    public OAuthController(KakaoService kakaoService, KakaoConfig kakaoConfig) {
        this.kakaoService = kakaoService;
        this.kakaoConfig = kakaoConfig;
    }

    @GetMapping("/login-url")
    public void getLoginUrl(HttpServletResponse response) {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoConfig.getClientId()+
                "&redirect_uri=" + kakaoConfig.getRedirectUri()+
                "&response_type=code" +
                "&scope=" + kakaoConfig.getScope();
        try {
            response.sendRedirect(kakaoLoginUrl);
        } catch (IOException e)
        {
            throw new RuntimeException("카카오 로그인 URL로 이동을 실패했습니다.");
        }
    }

    @PostMapping("/login-success")
    public ResponseEntity<String> loginSuccess(@RequestBody Map<String, String> loginData) {
        String token = loginData.get("token");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(400).body("토큰이 제공되지 않았습니다.");
        }

        try {
            // 카카오 토큰으로 이메일 및 인증 객체 생성
            kakaoService.handleKakaoToken(token, null);
            return ResponseEntity.ok("로그인 성공 및 인증 객체 설정 완료");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("로그인 처리 중 오류 발생: " + e.getMessage());
        }
    }

    @GetMapping("/save-user")
    public ResponseEntity<String> login(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.status(401).body("OAuth2User가 null입니다. 인증이 필요합니다. -controller");
        }

        String email = (String) oAuth2User.getAttributes().get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(400).body("이메일 정보를 가져올 수 없습니다.");
        }

        // 이메일 동의 여부를 기본값으로 설정 (API 응답 구조상 이미 검증됨)
        Boolean isEmailVerified = true;

        //초기 저장(이메일, 동의 여부, 소셜타입)
        kakaoService.SaveUser(email, isEmailVerified);

        return ResponseEntity.ok().header("Email", email).body("초기 저장이 성공적으로 저장되었습니다.");
    }

    @PostMapping("/update-user")
    public ResponseEntity<String> updateUser(@RequestBody User user, @RequestHeader("Email") String email){
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(400).body("헤더에 이메일이 포함되지 않았습니다.");
        }
        if(user==null){
            return ResponseEntity.status(400).body("유저 정보가 부족합니다.");
        }

        kakaoService.updateUser(email, user);

        return ResponseEntity.ok("사용자 정보를 성공적으로 업데이트하였습니다.");
    }
}
