package com.example.Roomy.SocialLogin.Util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String SECRET_KEY = "1234!";
    private final long EXPIRATION_TIME = 1;


    //토큰 생성
    public String createToken(String userId) {
        try {
            // JWT의 Payload 설정
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userId) // 사용자 ID
                    .issueTime(new Date()) // 토큰 생성 시간
                    .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
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
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("JWT 생성 실패: " + e.getMessage());
        }
    }

    //토큰 유효성 검사
    public boolean validateToken(String token) {
        try{
            SignedJWT signedJWT= SignedJWT.parse(token);
            JWSVerifier verifier=new MACVerifier(SECRET_KEY.getBytes());

            return signedJWT.verify(verifier);
        }
        catch (Exception e){
            return false;
        }
    }

    //토큰에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);

            return signedJWT.getJWTClaimsSet().getSubject();
        }
        catch(Exception e){
            throw new RuntimeException("파싱 실패");
        }
    }

    // getAuthentication 메서드 구현
    public UsernamePasswordAuthenticationToken getAuthentication(String userId) {
        // Spring Security의 UserDetails 객체 생성
        UserDetails userDetails = User.builder()
                .username(userId)
                .password("") // 패스워드가 없으므로 빈 문자열
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))) // 기본 역할
                .build();

        // 인증 객체 생성
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
