package com.example.Roomy.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    public String uploadImageToS3(MultipartFile file, int targetWidth, int targetHeight) throws IOException {
        // 원본 이미지를 읽어옵니다.
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        // targetWidth와 targetHeight에 맞는 크기로 새 이미지를 생성합니다.
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();

        // 원본 파일 이름에서 확장자를 제외하고 새로운 .jpg 이름을 생성합니다.
        String fileName = generateUniqueFileName("jpg");

        // 이미지를 .jpg 포맷으로 변환하여 저장합니다.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/jpeg");

        // 이미지를 S3에 업로드합니다.
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));

        return bucketUrl + "/" + fileName;
    }

    public void deleteFileFromS3(String fileUrl) {
        // S3에서 파일 이름을 추출하여 삭제합니다.
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        amazonS3.deleteObject(bucketName, fileName);
    }

    private String generateUniqueFileName(String extension) {
        // 랜덤 UUID를 생성하여 고유한 파일 이름을 반환합니다.
        return UUID.randomUUID().toString() + "." + extension;
    }
}
