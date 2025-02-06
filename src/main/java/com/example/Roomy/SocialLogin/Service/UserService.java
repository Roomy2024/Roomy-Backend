package com.example.Roomy.SocialLogin.Service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.SocialLogin.Entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User updateUserInfo(Long id, String username, int age, String area, String gender, String profile){
        User user = userRepository.findById(id).orElseThrow(()->new IllegalStateException("사용자 정보가 없습니다."));

        user.setUsername(username);
        user.setAge(age);
        user.setArea(area);
        user.setGender(gender);
        user.setProfile(profile);
        user.setRole(UserRole.MEMBER);

        return userRepository.save(user);
    }

}
