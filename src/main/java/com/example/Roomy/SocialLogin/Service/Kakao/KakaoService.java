package com.example.Roomy.SocialLogin.Service.Kakao;

import com.example.Roomy.SocialLogin.Dto.Kakao.KakaoAccessTokenResponse;
import com.example.Roomy.SocialLogin.Entity.Response.UserResponse;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.SocialLogin.Util.JwtTokenProvider;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

//카카오 인증 처리
@Service
public class KakaoService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    public KakaoService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.restTemplate = restTemplate;
    }

    //카카오 로그인 및 이메일 확인
    public String getEmailFromKakao(String authorizationCode){
        //액세스 토큰 요청
        String accessToken= getAccessToken(authorizationCode);

        //이메일 정보 요청
        return getEmail(accessToken);
    }

    //사용자 추가 정보를 저장 후 JWT 발급
    public String SaveUserInfo(String email, UserResponse userResponse){
        //사용자 정보를 저장 or 업데이트
        Optional<User> existingUser = userRepository.findBySocialIdAndSocialType(email,"Kakao");

        if(existingUser.isEmpty()){
            User newUser = new User();
            newUser.setSocialType("Kakao");
            newUser.setEmail(email);
            newUser.setUsername(userResponse.getUsername());
            newUser.setProfile(userResponse.getProfile());
            newUser.setAge(userResponse.getAge());
            newUser.setGender(userResponse.getGender());
            newUser.setArea(userResponse.getArea());
            userRepository.save(newUser);
        }

        //JWT 생성 및 반환
        return jwtTokenProvider.createToken(email);
    }
    //카카오 API를 통해 액세스 토큰 요청
    private String getAccessToken(String authorizationCode) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "0676830eb7f43efe2a27f8fb0d661db9"); // 카카오 REST API 키
        params.add("redirect_uri", "http://127.0.0.1:8080/callback"); // 리다이렉트 URI
        params.add("code", authorizationCode);

        //요청생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        //카카오 API 호출
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KakaoAccessTokenResponse> response = restTemplate.postForEntity(
                tokenUrl,
                request,
                KakaoAccessTokenResponse.class
        );

        return response.getBody().getAccessToken();
    }

    //카카오 api에서 이메일 요청
    private String getEmail(String accessToken){

    }
}
