package com.example.Roomy.SocialLogin.Service;

import com.example.Roomy.SocialLogin.DTO.UserRequest;
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
    public User updateUserInfo(UserRequest userRequest, Long id) {
        System.out.println("=========================================================");
        System.out.println("=========================================================");
        System.out.println(userRequest);
        System.out.println("=========================================================");
        System.out.println("=========================================================");

        User user = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));

        user.setUsername(userRequest.getUsername());
        //user.setAge(userRequest.getAge());
        user.setArea(userRequest.getArea());
        user.setGender(userRequest.getGender());
        user.setRole(UserRole.MEMBER);



        MultipartFile profileImage = userRequest.getProfileImage();

        // ✅ 프로필 이미지가 있다면 S3에 업로드
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                if (user.getProfile() != null) {
                    fileService.deleteFileFromS3(user.getProfile()); // 기존 프로필 삭제
                }
                String profileImageUrl = fileService.uploadUserProfileImageToS3(profileImage, user.getId());
                user.setProfile(profileImageUrl);
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage(), e);
            }
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserProfile(Long id, MultipartFile profileImage) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));

        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                if (user.getProfile() != null) {
                    fileService.deleteFileFromS3(user.getProfile()); // 기존 프로필 삭제
                }
                String profileImageUrl = fileService.uploadUserProfileImageToS3(profileImage, user.getId());
                user.setProfile(profileImageUrl);
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage(), e);
            }
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserName(Long id, String username){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));

        user.setUsername(username);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserArea(Long id, String area){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));

        user.setArea(area);
        return userRepository.save(user);
    }
}
