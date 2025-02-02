package com.example.Roomy.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    /**
     * 이미지 파일을 지정된 크기로 리사이즈 후 S3에 업로드하고 파일 URL을 반환합니다.
     *
     * @param file         업로드할 이미지 파일
     * @param targetWidth  리사이즈할 너비
     * @param targetHeight 리사이즈할 높이
     * @return 업로드된 이미지의 URL
     * @throws IOException 이미지 처리나 업로드 과정 중 오류가 발생하면 예외 발생
     */
    public String uploadImageToS3(MultipartFile file, int targetWidth, int targetHeight) throws IOException {
        // 파일 유효성 검사
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 null이거나 비어 있습니다.");
        }

        // 원본 이미지를 읽어옵니다.
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("이미지를 읽을 수 없습니다. 지원하지 않는 이미지 형식일 수 있습니다.");
        }

        // targetWidth와 targetHeight에 맞게 새 이미지 생성 (비율에 따른 왜곡 발생 가능)
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();

        // 고유 파일 이름 생성 (.jpg 확장자)
        String fileName = generateUniqueFileName("jpg");

        // 이미지를 JPEG 포맷으로 변환하여 바이트 배열로 저장
        byte[] imageBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (!ImageIO.write(resizedImage, "jpg", outputStream)) {
                throw new IOException("이미지를 JPEG 형식으로 변환하는데 실패했습니다.");
            }
            imageBytes = outputStream.toByteArray();
        } catch (IOException e) {
            logger.error("이미지 처리 중 오류 발생", e);
            throw e;
        }

        // S3 업로드를 위한 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/jpeg");

        // S3에 업로드 (ByteArrayInputStream을 사용하여 업로드)
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
        } catch (Exception e) {
            logger.error("S3 업로드 중 오류 발생", e);
            throw new IOException("S3 업로드 실패", e);
        }

        String fileUrl = bucketUrl + "/" + fileName;
        logger.info("이미지 업로드 성공: {}", fileUrl);
        return fileUrl;
    }

    /**
     * S3에 업로드된 파일 URL을 받아 해당 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 URL
     */
    public void deleteFileFromS3(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new IllegalArgumentException("파일 URL이 null이거나 비어 있습니다.");
        }
        // URL에서 파일 이름 추출 (마지막 '/' 이후의 문자열)
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        try {
            amazonS3.deleteObject(bucketName, fileName);
            logger.info("파일 삭제 성공: {}", fileName);
        } catch (Exception e) {
            logger.error("S3에서 파일 삭제 중 오류 발생: {}", fileName, e);
            throw e;
        }
    }

    /**
     * 지정된 확장자를 가진 고유한 파일 이름을 생성합니다.
     *
     * @param extension 파일 확장자 (예: "jpg")
     * @return 고유한 파일 이름
     */
    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }
}
