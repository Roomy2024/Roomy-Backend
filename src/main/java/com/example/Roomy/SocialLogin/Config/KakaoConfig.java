package com.example.Roomy.SocialLogin.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KakaoConfig {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.client_secret}")
    private String clientSecret;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @Value("${kakao.SECRET_KEY}")
    private String secretKey;

    @Value("${kakao.KAKAO_SOCIAL_TYPE}")
    private String socailType;

    @Value("${kakao.scope}")
    private String scope;
}
