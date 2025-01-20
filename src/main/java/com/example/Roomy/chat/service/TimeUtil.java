package com.example.Roomy.chat.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    // 타임스탬프를 한국 시간으로 변환하고 AM/PM 00:00 형식으로 반환하는 메서드
    public static String convertTimestampToKoreanTime(long timestamp) {
        // 타임스탬프를 ZonedDateTime으로 변환
        ZonedDateTime koreanTime = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.of("Asia/Seoul")); // 한국 표준 시간(KST)

        // AM/PM 00:00 형식으로 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh:mm"); // AM/PM hh:mm 형식
        return koreanTime.format(formatter);
    }

    public static void main(String[] args) {
        // 테스트용 타임스탬프 (예: 2025년 1월 14일 00:00:00)
        long timestamp = 1705190400000L; // Unix Epoch 밀리초
        String formattedTime = convertTimestampToKoreanTime(timestamp);
        System.out.println(formattedTime); // 출력: AM 12:00
    }
}
