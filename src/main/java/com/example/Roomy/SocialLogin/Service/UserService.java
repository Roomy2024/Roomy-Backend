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
    public User updateUserInfo(Long id, String username, int age, String area, String gender, MultipartFile profileImage) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));

        user.setUsername(username);
        user.setAge(age);
        user.setArea(area);
        user.setGender(gender);
        user.setRole(UserRole.MEMBER);

        //프로필 이미지가 있다면 S3에 업로드 (예외 처리 추가)
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                if (user.getProfile() != null) {
                    fileService.deleteFileFromS3(user.getProfile()); // 기존 프로필 삭제
                }
                String profileImageUrl = fileService.uploadUserProfileImageToS3(profileImage);
                user.setProfile(profileImageUrl);
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage(), e);
            }
        }
        return userRepository.save(user);
    }
}
