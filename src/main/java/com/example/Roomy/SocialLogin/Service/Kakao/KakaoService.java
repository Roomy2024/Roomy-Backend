package com.example.Roomy.SocialLogin.Service.Kakao;

import com.example.Roomy.SocialLogin.Config.KakaoConfig;
import com.example.Roomy.SocialLogin.Dto.Redis.RefreshTokenDTO;
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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public void SaveUser(String email, boolean isEmailVerified){
        //사용자 정보가 있는지 확인
        Optional<User> existUser=userRepository.findByEmailAndSocialType(email,kakaoConfig.getSocailType());

        if(existUser.isEmpty()){
            User newUser=new User();
            newUser.setSocialType(kakaoConfig.getSocailType());
            newUser.setEmail(email);
            newUser.setEmailAgree(isEmailVerified);
            userRepository.save(newUser);
        }
    }

    //이메일, 소셜타입을 먼저 저장해두고 사용자 입력 정보 업데이트
    public String updateUser(String email, User userInfo){
        Optional<User> existingUser = userRepository.findByEmailAndSocialType(email,kakaoConfig.getSocailType());

        //같은 이메일이 있다면
        if(existingUser.isPresent()){
            User user = existingUser.get();
            user.setUsername(userInfo.getUsername());
            user.setProfile(userInfo.getProfile());
            user.setAge(userInfo.getAge());
            user.setGender(userInfo.getGender());
            user.setArea(userInfo.getArea());

            Long id=existingUser.get().getId();

            userRepository.save(user);
            return jwtTokenProvider.createToken(id);
        }
        else{
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }

    public void handleKakaoToken(String token, HttpServletRequest request) {
        try {
            String email = getEmailFromToken(token);
            if (email == null || email.isEmpty()) {
                throw new RuntimeException("유효하지 않은 카카오 토큰입니다.");
            }

            // OAuth2User 구현체 생성
            Map<String, Object> attributes = Map.of("email", email);
            OAuth2User oAuth2User = new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "email"
            );

            // 인증 객체 생성 및 SecurityContext에 설정
            Authentication authentication = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Authentication created: " + SecurityContextHolder.getContext().getAuthentication());
        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 처리 중 오류 발생: " + e.getMessage());
        }
    }

    public String getEmailFromToken(String token) {
        String url = "https://kapi.kakao.com/v2/user/me";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); // Bearer token 전달

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 카카오 API 호출하여 사용자 정보 가져오기
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // 응답에서 카카오 계정 정보 추출
                Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
                if (kakaoAccount != null) {
                    return (String) kakaoAccount.get("email"); // 이메일 반환
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 처리 실패: " + e.getMessage());
        }
        return null; // 이메일을 추출하지 못한 경우
    }


    // 인증 객체 생성
    public Authentication createAuthentication(String email) {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(email, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    //이메일 추출
    public String getEmail(OAuth2User oAuth2User){
        if (oAuth2User == null) {
            throw new RuntimeException("OAuth2User가 null입니다. 인증이 필요합니다. - service");
        }
        // 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        // 이메일 추출
        String email = (String) kakaoAccount.get("email");
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("이메일 정보를 가져올 수 없습니다.");
        }

        // 이메일 반환
        return email;
    }

    //이메일 동의 여부
    public Boolean getEmailAgree(OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            throw new RuntimeException("OAuth2User가 null입니다. 인증이 필요합니다.");
        }

        // 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        // 이메일 동의 여부 추출
        Boolean isEmailVerified = (Boolean) kakaoAccount.get("is_email_verified");
        if (isEmailVerified == null) {
            throw new RuntimeException("이메일 동의 여부 정보를 가져올 수 없습니다.");
        }

        // 동의 여부 반환
        return isEmailVerified;
    }
//    public String getEmailFromToken(String token) {
//        try {
//            //전달 받은 JWT 토큰 문자열을 SignedJWT 객체로 파싱 / 서명정보 추출 가능
//            SignedJWT signedJWT = SignedJWT.parse(token);
//
//            //SECRET_KEY를 사용하여 검증 / HMAC-SHA256 알고리즘을 사용하므로 MACVerifier를 생성
//            JWSVerifier jwsVerifier = new MACVerifier(kakaoConfig.getSecretKey().getBytes());
//            if (!signedJWT.verify(jwsVerifier)) {
//                throw new RuntimeException("토큰 검증 실패");
//            }
//
//            //토큰에 포함된 클레임 가져오기 ex)사용자 정보, 만료시간 등
//            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
//
//            //토큰 만료 시간 검증
//            Date expirationTime = jwtClaimsSet.getExpirationTime();
//            if (expirationTime == null || expirationTime.before(new Date())) {
//                //만료 시간이 없거나 만료시간보다 이전인 경우
//                throw new RuntimeException("토큰이 만료되었습니다.");
//            }
//            //사용자 식별을 위해 이메일 반환 ex)글 쓰기, 댓글 쓰기 등
//            return jwtClaimsSet.getSubject();
//        }
//        catch (ParseException | JOSEException ex){
//            throw new RuntimeException("토큰 처리 중 에러 발생: " + ex.getMessage());
//        }
//    }

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
