package com.example.Roomy.SocialLogin.Service.Kakao;

import com.example.Roomy.SocialLogin.Config.KakaoConfig;
import com.example.Roomy.SocialLogin.Dto.Kakao.KakaoAccessTokenResponse;
import com.example.Roomy.SocialLogin.Dto.Kakao.KakaoUserResponse;
import com.example.Roomy.SocialLogin.Dto.Redis.RefreshTokenDTO;
import com.example.Roomy.SocialLogin.Dto.User.UserResponse;
import com.example.Roomy.SocialLogin.Entity.RefreshToken;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.RedisRepository;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.SocialLogin.Util.JwtTokenProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

import static javax.crypto.Cipher.SECRET_KEY;

//카카오 인증 처리
@Service
public class KakaoService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisRepository redisRepository;
   private final KakaoConfig kakaoConfig;

    public KakaoService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, RedisRepository redisRepository, KakaoConfig kakaoConfig) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisRepository = redisRepository;
        this.kakaoConfig = kakaoConfig;
    }

    //OAuth에서 이메일 추출, 사용자 정보 저장
    public void OAuthUser(OAuth2User oAuth2User){
        //이메일 가져옴
        String email = (String) oAuth2User.getAttributes().get("account_email");

        if(email==null || email.isEmpty()){
            throw new RuntimeException("이메일을 찾을 수 없습니다.");
        }

        //이메일 동의 여부 확인
        boolean emailAgree = (Boolean) oAuth2User.getAttributes().get("emailAgree");

        if(!emailAgree){
            throw new RuntimeException("이메일 동의가 필요합니다");
        }

        //사용자 정보가 있는지 확인
        Optional<User> existUser=userRepository.findByEmailAndSocialType(email,kakaoConfig.getSocailType());

        if(existUser.isEmpty()){
            User newUser=new User();
            newUser.setSocialType(kakaoConfig.getSocailType());
            newUser.setEmail(email);
            userRepository.save(newUser);
        }
    }

    //이메일, 소셜타입을 먼저 저장해두고 사용자 입력 정보 업데이트
    public String UserInfo(String email, User userInfo, Long id, String username){
        Optional<User> existingUser = userRepository.findByEmailAndSocialType(email,kakaoConfig.getSocailType());

        if(existingUser.isPresent()){
            User user = existingUser.get();
            user.setUsername(userInfo.getUsername());
            user.setProfile(userInfo.getProfile());
            user.setAge(userInfo.getAge());
            user.setGender(userInfo.getGender());
            user.setArea(userInfo.getArea());

            //이메일 동의 여부
            Boolean emailAgree=userInfo.isEmailAgree();
            if(!emailAgree)
            {
                throw new RuntimeException("이메일 동의가 필요합니다.");
            }
            user.setEmailAgree(emailAgree);
            userRepository.save(user);

            return jwtTokenProvider.createToken(email, username, id);
        }
        else{
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }

    public String getEmailFromToken(String token) {
        try {
            //전달 받은 JWT 토큰 문자열을 SignedJWT 객체로 파싱 / 서명정보 추출 가능
            SignedJWT signedJWT = SignedJWT.parse(token);

            //SECRET_KEY를 사용하여 검증 / HMAC-SHA256 알고리즘을 사용하므로 MACVerifier를 생성
            JWSVerifier jwsVerifier = new MACVerifier(kakaoConfig.getSecretKey().getBytes());
            if (!signedJWT.verify(jwsVerifier)) {
                throw new RuntimeException("토큰 검증 실패");
            }

            //토큰에 포함된 클레임 가져오기 ex)사용자 정보, 만료시간 등
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

            //토큰 만료 시간 검증
            Date expirationTime = jwtClaimsSet.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                //만료 시간이 없거나 만료시간보다 이전인 경우
                throw new RuntimeException("토큰이 만료되었습니다.");
            }
            //사용자 식별을 위해 이메일 반환 ex)글 쓰기, 댓글 쓰기 등
            return jwtClaimsSet.getSubject();
        }
        catch (ParseException | JOSEException ex){
            throw new RuntimeException("토큰 처리 중 에러 발생: " + ex.getMessage());
        }
    }

    //refresh token 저장
    private void saveRefreshToken(String email, String socialType, String refreshTokenValue , Long expirationTime){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(refreshTokenValue);
        //Radis에 저장
        RefreshTokenDTO tokenDTO = RefreshTokenDTO.from(email,refreshTokenValue,expirationTime);
        redisRepository.save(tokenDTO);

        //DB에 저장
        Optional<User> existingUser = userRepository.findByEmailAndSocialType(email,socialType);
        if(existingUser.isPresent()){
            User user = existingUser.get();
            //refreshToken 업데이트
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }
        else{
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }

}
