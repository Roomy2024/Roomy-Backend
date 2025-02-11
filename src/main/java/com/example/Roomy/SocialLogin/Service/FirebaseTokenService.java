package com.example.Roomy.SocialLogin.Service;

import com.google.firebase.auth.FirebaseAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseTokenService {

    // FCM 토큰을 생성하는 메서드
    public String generateFirebaseToken(String userId) {
        try {
            // Firebase Auth에서 사용자 정의 토큰 생성
            return FirebaseAuth.getInstance().createCustomToken(userId);
        } catch (Exception e) {
            throw new RuntimeException("FCM 토큰 생성 실패", e);
        }
    }
}
