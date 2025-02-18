package com.example.Roomy.SocialLogin.JWT;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String SECRET_KEY = "You-Have-Very-Strong-Secret-Key-123456789!"; // 반드시 안전하게 관리할 것!
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 30 * 60 * 1000L; //30분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 30L * 24 * 60 * 60 * 1000L; // 30일

    private final UserRepository userRepository;


    //토큰 생성
    public String createAccessToken(Long userId) {
        try {
            // JWT의 Payload 설정
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .claim("id", userId)
                    .issueTime(new Date()) // 토큰 생성 시간
                    .expirationTime(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME)) // 만료 시간
                    .build();
            // HMAC-SHA256 서명 알고리즘으로 서명 생성
            JWSSigner signer = new MACSigner(SECRET_KEY.getBytes());
            // JWT 생성 및 서명
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256), // 헤더
                    claimsSet // 페이로드
            );
            signedJWT.sign(signer);
            // 직렬화된 JWT 반환

            System.out.println("JWT 토큰 생성: "+signedJWT.serialize());
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("JWT 생성 실패: " + e.getMessage());
        }
    }

    // Refresh Token 생성
    public String createRefreshToken(Long userId) {
        try {
            // JWT의 Payload 설정
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .claim("id", userId) // 사용자 ID를 Claim에 추가
                    .issueTime(new Date()) // 토큰 생성 시간
                    .expirationTime(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME)) // 만료 시간: 30일
                    .build();

            // HMAC-SHA256 서명 알고리즘으로 서명 생성
            JWSSigner signer = new MACSigner(SECRET_KEY.getBytes());

            // JWT 생성 및 서명
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256), // 헤더
                    claimsSet // 페이로드
            );
            signedJWT.sign(signer);

            // 직렬화된 JWT 반환
            System.out.println("Refresh Token 생성: " + signedJWT.serialize());
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Refresh Token 생성 실패: " + e.getMessage());
        }
    }

    // Refresh Token 검증 및 Access Token 재발급
    public String recreateAccessToken(String refreshToken) {
        try {
            // Refresh Token 파싱 및 유효성 검사
            SignedJWT signedJWT = SignedJWT.parse(refreshToken);
            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
            if (!signedJWT.verify(verifier)) {
                throw new IllegalArgumentException("Refresh Token 검증 실패");
            }

            // 만료 여부 확인
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime.before(new Date())) {
                throw new IllegalArgumentException("Refresh Token 만료됨");
            }

            // 사용자 ID 추출
            Long userId = signedJWT.getJWTClaimsSet().getLongClaim("id");

            // Refresh Token 확인
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            if (!refreshToken.equals(user.getRefreshToken())) {
                throw new IllegalArgumentException("Refresh Token 불일치");
            }

            // 새로운 Access Token 생성
            return createAccessToken(user.getId());
        } catch (Exception e) {
            throw new RuntimeException("Access Token 재발급 실패: " + e.getMessage());
        }
    }

    //토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
            return signedJWT.verify(verifier);
        } catch (Exception e) {
            return false;
        }
    }

    // Request의 Header에서 token 값 가져오기
    public String getTokenFromRequest(HttpServletRequest request) {
        System.out.println(request.getHeader("Authorization"));
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {

            return token.substring(7); // "Bearer " 제거
        }
        return null;
    }

    //토큰에서 사용자ID 추출
    public String getUserIdFromToken(String token) {
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);
            return String.valueOf(signedJWT.getJWTClaimsSet().getClaim("id"));
        }
        catch(Exception e){
            throw new RuntimeException("파싱 실패");
        }
    }

    //인증정보 조회
    public UsernamePasswordAuthenticationToken getAuthentication(String userId) {
        // Spring Security의 UserDetails 객체 생성
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(userId)
                .password("") // 패스워드가 없으므로 빈 문자열
                //.authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))) // 기본 역할
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
