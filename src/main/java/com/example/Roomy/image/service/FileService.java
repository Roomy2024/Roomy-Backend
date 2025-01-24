package com.example.Roomy.image.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FileService {

    private final String baseUploadDir = "src/main/resources/static/imgs";

    public FileService() {
        File directory = new File(baseUploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    // 이미지 저장 및 형식 통일, 해상도 조정
    public String saveAndResizeImage(MultipartFile file, int targetWidth, int targetHeight) throws IOException {
        // 원본 이미지 읽기
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("이미지 파일이 아닙니다.");
        }

        // 해상도 조정
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();

        // 저장 경로 생성
        LocalDate today = LocalDate.now();
        String datePath = today.getYear() + "/" + today.getMonthValue() + "/" + today.getDayOfMonth();
        Path directoryPath = Paths.get(baseUploadDir, datePath);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // 파일 이름 설정 (공백 제거 + 랜덤 UUID)
        String originalFileName = file.getOriginalFilename().replaceAll("\\s+", "");
        String fileName = UUID.randomUUID() + "_" + originalFileName.replaceAll("\\..+$", ".jpg"); // 확장자 통일

        // 파일 저장
        Path filePath = directoryPath.resolve(fileName);
        ImageIO.write(resizedImage, "jpg", filePath.toFile());

        return "/imgs/" + datePath + "/" + fileName;
    }
}

