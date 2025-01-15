package com.example.Roomy.SocialLogin.Dto.Google;

import com.example.Roomy.SocialLogin.Dto.Kakao.KakaoUserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoogleUserResponse {

    private Long id;

    @JsonProperty("google_account")
    private GoogleUserResponse.GoogleAccount googleAccount;

    @Data
    public static class GoogleAccount{
        private String email;
    }
}
