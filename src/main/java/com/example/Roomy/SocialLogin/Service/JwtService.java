package com.example.Roomy.SocialLogin.Service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public String recreateAccessToken(String refreshToken) {
        // 1. Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired Refresh Token");
        }

        // 2. Refresh Token에서 사용자 ID 추출
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 3. 사용자 조회 및 Refresh Token 비교
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new IllegalArgumentException("Refresh Token mismatch");
        }

        // 4. 새로운 Access Token 생성
        return jwtTokenProvider.createAccessToken(user.getId());
    }
}
