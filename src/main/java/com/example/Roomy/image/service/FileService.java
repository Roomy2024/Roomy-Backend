package com.example.Roomy.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.bucket-url}")
    private String bucketUrl;

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    /**
     * 사용자 프로필 이미지를 S3의 userprofile 폴더에 업로드
     */
    public String uploadUserProfileImageToS3(MultipartFile file) throws IOException {
        return uploadImageToS3(file, "userprofile/", 300, 300);
    }

    /**
     * 커뮤니티 이미지를 S3의 communityimage 폴더에 업로드
     */
    public String uploadCommunityImageToS3(MultipartFile file) throws IOException {
        return uploadImageToS3(file, "communityimage/", 800, 600);
    }

    /**
     * S3에 이미지 업로드
     * @param file 업로드할 이미지 파일
     * @param folder S3 폴더 경로 (userprofile/ 또는 communityimage/)
     * @param targetWidth 리사이즈할 너비
     * @param targetHeight 리사이즈할 높이
     * @return 업로드된 이미지의 URL
     */
    private String uploadImageToS3(MultipartFile file, String folder, int targetWidth, int targetHeight) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("이미지 형식을 읽을 수 없습니다.");
        }

        BufferedImage resizedImage = resizeImage(originalImage, targetWidth, targetHeight);
        byte[] imageBytes = convertToByteArray(resizedImage, "jpg");

        String fileName = folder + generateUniqueFileName("jpg"); // ✅ 폴더 경로 추가
        uploadToS3(fileName, imageBytes, "image/jpeg");

        return bucketUrl + "/" + fileName;
    }

    /**
     * 기본 프로필 이미지를 S3의 userprofile 폴더에 업로드
     */
    public String uploadDefaultProfileImageToS3() throws IOException {
        ClassPathResource defaultImageResource = new ClassPathResource("static/defaultImage.png");
        byte[] defaultImageBytes = Files.readAllBytes(defaultImageResource.getFile().toPath());

        String fileName = "userprofile/defaultProfile.jpg";
        uploadToS3(fileName, defaultImageBytes, "image/jpeg");

        return bucketUrl + "/" + fileName;
    }

    /**
     * S3에 파일 업로드
     */
    private void uploadToS3(String fileName, byte[] imageBytes, String contentType) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType(contentType);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
        } catch (Exception e) {
            logger.error("S3 업로드 중 오류 발생", e);
            throw new IOException("S3 업로드 실패", e);
        }
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
        logger.info("파일 삭제 성공: {}", fileName);
    }

    /**
     * 이미지 리사이징
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(originalImage, 0, 0, width, height, null);
        graphics.dispose();
        return resizedImage;
    }

    /**
     * 이미지 바이트 배열 변환
     */
    private byte[] convertToByteArray(BufferedImage image, String format) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 고유 파일 이름 생성
     */
    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }
}
