package com.example.Roomy.SocialLogin.Dto.Redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

//조회와 만료 처리를 위해 사용
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("refreshToken")
public class RefreshTokenDTO {
    @Id
    private String email;

    private String refreshToken;

    //Radis에서 자동 만료 시간 설정
    @TimeToLive
    private Long expiration;

    //DTO 생성
    public static RefreshTokenDTO from(String email, String refreshToken, Long expirationTime){
        return RefreshTokenDTO.builder()
                .email(email)
                .refreshToken(refreshToken)
                .expiration(expirationTime/1000)
                .build();
    }
}
