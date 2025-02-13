package com.example.Roomy.MyPage.Service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.image.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserInfoUpdateService {

    private final UserRepository userRepository;
    private final FileService fileService;

    @Transactional
    public User updateUserProfile(Long id, MultipartFile profileImage) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));

        String profileImageUrl;

        try {
            if (profileImage != null && !profileImage.isEmpty()) {
                if (user.getProfile() != null) {
                    fileService.deleteFileFromS3(user.getProfile());
                }
                profileImageUrl = fileService.uploadUserProfileImageToS3(profileImage);
            } else {
                profileImageUrl = fileService.uploadDefaultProfileImageToS3();
            }
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage(), e);
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
