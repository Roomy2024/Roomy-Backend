package com.example.Roomy.SocialLogin.Dto.Kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

//API에서 액세스 토큰 요청시 반환되는 응답데이터를 매ㅣㅇ
@Data
public class KakaoAccessTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("scope")
    private String scope;
}
