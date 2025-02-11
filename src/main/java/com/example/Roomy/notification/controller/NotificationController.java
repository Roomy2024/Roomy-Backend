package com.example.Roomy.notification.controller;

import com.example.Roomy.notification.dto.NotificationDTO;
import com.example.Roomy.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 사용자의 알림 목록 조회 API
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 특정 알림 삭제 API (본인만 삭제 가능)
     */
    @DeleteMapping("/{requesterId}/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long requesterId, @PathVariable Long notificationId) {
        notificationService.deleteNotification(requesterId, notificationId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{requesterId}/all/{userId}")
    public ResponseEntity<Void> deleteAllNotifications(@PathVariable Long requesterId, @PathVariable Long userId) {
        notificationService.deleteAllNotifications(requesterId, userId);
        return ResponseEntity.noContent().build();
    }
}
