package com.example.Roomy.SocialLogin.Dto.Kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoUserResponse {

    private Long id; //사용자 고유 id

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Data
    public static class KakaoAccount{
        private String email;
    }

    @Data
    public static class kakaoAccessTokenResponse{
        @JsonProperty("access_token")
        private String accessToken;
    }
}
