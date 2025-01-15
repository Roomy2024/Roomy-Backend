package com.example.Roomy.SocialLogin.Controller.Kakao;

import com.example.Roomy.SocialLogin.Config.KakaoConfig;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Service.Kakao.KakaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> getLoginUrl() {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoConfig.getClientId()+
                "&redirect_uri=" + kakaoConfig.getRedirectUri()+
                "&response_type=code" +
                "&scope=" + kakaoConfig.getScope();
        return ResponseEntity.ok(kakaoLoginUrl);
    }

    @GetMapping("/save-user")
    public ResponseEntity<String> login(@AuthenticationPrincipal OAuth2User oAuth2User) {
        System.out.println("아 개짜증나");
        if (oAuth2User == null) {
            return ResponseEntity.status(401).body("OAuth2User가 null입니다. 인증이 필요합니다.");
        }
        kakaoService.OAuthUser(oAuth2User);
        return ResponseEntity.ok("카카오 로그인 성공!");
    }

    @PostMapping("/update-user")
    public ResponseEntity<String> updateUser(@RequestBody User user, @RequestHeader("Authorization") String token){
        //헤더에서 이메일 추출
        String getEmail = kakaoService.getEmailFromToken(token.replace("Bearer ",""));
        kakaoService.UserInfo(user.getEmail(), user, user.getId(), user.getUsername());

        return ResponseEntity.ok("사용자 정보를 성공적으로 업데이트하였습니다.");
    }
}
