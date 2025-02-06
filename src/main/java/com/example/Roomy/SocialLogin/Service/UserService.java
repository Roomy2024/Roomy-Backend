package com.example.Roomy.SocialLogin.Service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.SocialLogin.Entity.UserRole;
import com.example.Roomy.image.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileService fileService;

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

    @Transactional
    public User updateUserProfile(Long id, MultipartFile profileImage) throws IOException {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));

        String profileImageUrl;

        if (profileImage != null && !profileImage.isEmpty()) {
            if (user.getProfile() != null) {
                fileService.deleteFileFromS3(user.getProfile());
            }
            profileImageUrl = fileService.uploadImageToS3(profileImage, 300, 300);
        } else {
            profileImageUrl = fileService.uploadDefaultProfileImageToS3();
        }

        user.setProfile(profileImageUrl);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserName(Long id, String username){
        User user = userRepository.findById(id).orElseThrow(()->new IllegalStateException("사용자 정보가 없습니다."));

        user.setUsername(username);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserArea(Long id, String area){
        User user = userRepository.findById(id).orElseThrow(()->new IllegalStateException("사용자 정보가 없습니다."));

        user.setArea(area);
        return userRepository.save(user);
    }
}
