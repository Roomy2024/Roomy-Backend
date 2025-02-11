package com.example.Roomy.notification.service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.notification.dto.NotificationDTO;
import com.example.Roomy.notification.entity.NotificationEntity;
import com.example.Roomy.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

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

        NotificationEntity notification = NotificationEntity.builder()
                .user(receiver) // 알림 수신자
                .sender(sender) // 알림 발신자
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
}
