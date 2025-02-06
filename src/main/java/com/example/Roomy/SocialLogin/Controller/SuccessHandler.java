package com.example.Roomy.SocialLogin.Controller;


import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.SocialLogin.Entity.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuccessHandler implements AuthenticationSuccessHandler {

    private static final String REDIRECT_URI = "http://localhost:3000/";
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 인증된 사용자 정보를 OAuth2User 객체로 가져옴
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 인증 공급자(Kakao, Google 등)의 이름 가져오기
        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        // OAuth2User에서 이메일 정보를 추출 (공급자별로 처리 방식 다름)
        String email = extractEmailFromOAuth2User(provider, oAuth2User);

        // 이메일 정보가 없을 경우 에러 처리
        if (email == null) {
            log.error("OAuth2 인증 성공했지만 이메일 정보를 가져올 수 없습니다. provider: {}", provider);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "이메일 정보를 가져올 수 없습니다.");
            return;
        }

        log.info("OAuth2 인증 성공. provider: {}, email: {}", provider, email);

        // 이메일로 사용자 조회, 없으면 새로운 사용자 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // 새 사용자 생성
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setProvider(provider); // 인증 공급자 정보 저장
                    newUser.setRole(UserRole.MEMBER); // 기본 역할 설정
                    return userRepository.save(newUser); // 저장 후 반환
                });

        // 6. Access Token 및 Refresh Token 생성
        String accessToken;
        String refreshToken;
        try {
            accessToken = jwtTokenProvider.createAccessToken(user.getId());
            refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        } catch (Exception e) {
            log.error("토큰 생성 중 오류 발생: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "토큰 생성 실패");
            return;
        }

        // Refresh Token을 DB에 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user); // 사용자 정보 업데이트

        log.info("Access Token 및 Refresh Token 생성 완료: userId = {}", user.getId());

        // 클라이언트에 리다이렉트, 토큰 전달 (헤더로 전달)
        response.sendRedirect(String.format("%s?access_token=%s&refresh_token=%s&userid=%d", REDIRECT_URI, accessToken, refreshToken, user.getId()));
    }


    private String extractEmailFromOAuth2User(String provider, OAuth2User oAuth2User) {
        switch (provider) {
            case "kakao":
                // 카카오: "kakao_account" 안에 email 정보가 포함됨
                Object kakaoAccountObj = oAuth2User.getAttribute("kakao_account");
                if (kakaoAccountObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
                    if (kakaoAccount.containsKey("email")) {
                        return (String) kakaoAccount.get("email");
                    } else {
                        log.warn("카카오 계정에 이메일 정보가 없습니다.");
                    }
                } else {
                    log.warn("카카오 계정 정보를 가져올 수 없습니다.");
                }
                break;
            case "google":
                // 구글: "email" 필드에서 직접 가져올 수 있음
                return oAuth2User.getAttribute("email");
            default:
                log.warn("지원되지 않는 OAuth2 제공자: {}", provider);
                return null;
        }
        return null; // 이메일 정보가 없으면 null 반환
    }
}