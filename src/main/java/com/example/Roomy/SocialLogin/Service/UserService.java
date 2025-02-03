package com.example.Roomy.SocialLogin.Service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User updateUserInfo(String email, String username, int age, String area, String gender, String profile){
        User user = userRepository.findByEmail(email).orElseThrow(()->new IllegalStateException("사용자 정보가 없습니다."));

        user.setUsername(username);
        user.setAge(age);
        user.setArea(area);
        user.setGender(gender);
        user.setProfile(profile);

        return userRepository.save(user);
    }

    @Transactional
    public void saveRefreshToken(User user, String refreshToken) {
        // 사용자 엔티티에 Refresh Token 저장
        user.setRefreshToken(refreshToken);

        // 변경된 사용자 정보 저장
        userRepository.save(user);
    }
}
