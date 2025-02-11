package com.example.Roomy.notification.service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.notification.dto.NotificationDTO;
import com.example.Roomy.notification.entity.NotificationEntity;
import com.example.Roomy.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.Roomy.notification.service.FCMService;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FCMService fcmService;

    /**
     * 특정 사용자의 알림 목록 조회
     */
    public List<NotificationDTO> getUserNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<NotificationEntity> notifications = notificationRepository.findByUserId(userId);

        return notifications.stream()
                .map(notification -> new NotificationDTO(
                        notification.getId(),
                        notification.getSender().getUsername(),
                        notification.getMessage(),
                        notification.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 특정 알림 삭제 (본인만 삭제 가능)
     */
    @Transactional
    public void deleteNotification(Long requesterId, Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!requesterId.equals(notification.getUser().getId())) {
            throw new IllegalStateException("You can only delete your own notifications.");
        }

        notificationRepository.delete(notification);
    }

    /**
     * 사용자의 모든 알림 삭제 (본인만 가능)
     */
    @Transactional
    public void deleteAllNotifications(Long requesterId, Long userId) {
        if (!requesterId.equals(userId)) {
            throw new IllegalStateException("You can only delete your own notifications.");
        }

        List<NotificationEntity> notifications = notificationRepository.findByUserId(userId);
        notificationRepository.deleteAll(notifications);
    }

    /**
     * 알림 생성 및 저장
     */

    @Transactional
    public void sendNotification(Long senderId, Long receiverId, String message) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // **DB에 알림 저장**
        NotificationEntity notification = NotificationEntity.builder()
                .user(receiver) // 알림 수신자
                .sender(sender) // 알림 발신자
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // **FCM 토큰 유효성 검사**
        String fcmToken = receiver.getFcmToken();
        if (StringUtils.isBlank(fcmToken)) { // ✅ null, 빈 값 체크
            System.err.println("❌ 알림 전송 실패: 수신자의 FCM 토큰이 없습니다. / 개발중");
            return;
        }

        // **FCM 토큰 형식 검증 (예제: 길이가 140자 이상인지 체크)**
        if (fcmToken.length() < 140) {
            System.err.println("❌ 알림 전송 실패: 유효하지 않은 FCM 토큰 / 개발중");
            return;
        }

        // **푸시 알림 전송**
        try {
            fcmService.sendPushNotification(fcmToken, "새로운 알림", message);
            System.out.println("✅ FCM 알림 전송 성공: " + message);
        } catch (Exception e) {
            System.err.println("❌ FCM 알림 전송 실패: " + e.getMessage());
        }
    }

}
