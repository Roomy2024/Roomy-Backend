package com.example.Roomy.MyPage.Service;

import com.example.Roomy.SocialLogin.DTO.UserDTO;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.community.dto.CommunityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;

    @Transactional
    public UserDTO ShowMyPage(Long id){
        User user = userRepository.findById(id).orElseThrow(()->new IllegalStateException("사용자 정보가 없습니다."));

        return new UserDTO(
                user.getUsername(),
                user.getEmail(),
                user.getArea(),
                user.getGender(),
                user.getAge(),
                user.getProfile()
                );
    }
}