package com.example.Roomy.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.bucket-url}")
    private String bucketUrl;

    /**
     * 유저 프로필 이미지를 S3의 userprofile/유저id 폴더에 업로드
     */
    public String uploadUserProfileImageToS3(MultipartFile file, Long userId) throws IOException {
        return uploadImageToS3(file, "userprofile/" + userId + "/");
    }

    /**
     * 커뮤니티 이미지를 S3의 communityimages/글제목 폴더에 업로드
     */
    public String uploadCommunityImageToS3(MultipartFile file, String title) throws IOException {
        return uploadImageToS3(file, "communityimages/" + title + "/");
    }

    /**
     * S3에 이미지 업로드
     * @param file 업로드할 이미지 파일
     * @param folder S3 폴더 경로 (userprofile/userId/ 또는 communityimages/글제목/)
     * @return 업로드된 이미지의 URL
     */
    private String uploadImageToS3(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + extension;

        String keyName = folder + newFileName;
        String fileUrl;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucketName, keyName, file.getInputStream(), metadata));
            
            fileUrl = amazonS3.getUrl(bucketName, keyName).toString();
        } catch (IOException e) {
            throw new IOException("파일 업로드 실패", e);
        }

        return fileUrl;
    }

    /**
     * S3에 업로드된 파일 삭제
     */
    public void deleteFileFromS3(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new IllegalArgumentException("삭제할 파일 URL이 없습니다.");
        }

        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        amazonS3.deleteObject(bucketName, fileName);
    }
}
