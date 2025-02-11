package com.example.Roomy.notification.service;


import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    /**
     * FCM 푸시 알림 전송 메서드
     */
    public void sendPushNotification(String fcmToken, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ FCM 메시지 전송 성공: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("❌ FCM 메시지 전송 실패: " + e.getMessage());
        }
    }
}
