package com.example.Roomy.SocialLogin.Service;

import com.example.Roomy.SocialLogin.Entity.CustomUserDetails;
import com.example.Roomy.SocialLogin.Entity.OAuth2UserInfo;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. OAuth2 로그인 유저 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        // 2. provider : kakao, google
        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("provider : {}", provider);

        // 3. 필요한 정보를 provider에 따라 다르게 mapping
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(provider, oAuth2User.getAttributes());
        log.info("oAuth2UserInfo : {}", oAuth2UserInfo.toString());

        // 4. DB에서 이메일 조회
        User user = userRepository.findByEmail(oAuth2UserInfo.getEmail()).orElse(null);

        if (user != null) {
            // 이메일이 이미 존재하는 경우 provider를 비교하여 로그인할지, 회원가입할지 결정
            String userProvider = user.getProvider(); // 저장되어 있는 이메일의 provider를 가져옴

            if (userProvider.equals(provider)) {
                log.info("provider={}로 로그인한 email={} 정보가 있습니다.",userProvider,oAuth2UserInfo.getEmail());

                throw new OAuth2AuthenticationException("이미 회원가입 된 이메일입니다.");
            } else {
                // provider가 다를 경우 예외
                log.warn("이 이메일을 사용할 수 없습니다.: email={}, provider={}", oAuth2UserInfo.getEmail(), userProvider);
                // 사용자에게 반환할 메세지
                throw new OAuth2AuthenticationException("이 이메일을 사용할 수 없습니다. " + userProvider + "로 로그인을 시도해주세요");
            }
        }

        // 5. 새로운 유저 저장
        User newUser = oAuth2UserInfo.toEntity(); // OAuth2UserInfo에서 새로운 유저 엔티티 생성
        userRepository.save(newUser); // DB에 저장
        log.info("user: {}", newUser);

        // 6. UserDetails와 OAuth2User를 다중 상속한 CustomUserDetails 반환
        return new CustomUserDetails(newUser, oAuth2User.getAttributes());

    }

    public User saveRefreshToken(User user, String refreshToken){
        user.setRefreshToken(refreshToken);
        return userRepository.save(user);
    }
}