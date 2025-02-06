package com.example.Roomy.SocialLogin.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class OauthController {

    @GetMapping("/kakao/login-url")
    public Map<String, String> getKakaoLoginUrl(){
        String actualLoginPath = "http://localhost:8000/api/oauth2/authorization/kakao";

        return Collections.singletonMap("loginUrl", actualLoginPath);
    }

    @GetMapping("/google/login-url")
    public Map<String, String> getGoogleLoginUrl(){
        String actualLoginPath = "http://localhost:8000/api/oauth2/authorization/google";

        return Collections.singletonMap("loginUrl", actualLoginPath);
    }
}
