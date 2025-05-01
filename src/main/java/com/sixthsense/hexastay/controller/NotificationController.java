package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.NotificationDTO;
import com.sixthsense.hexastay.entity.Notification;
import com.sixthsense.hexastay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/roommenu/notifications")
@RequiredArgsConstructor

public class NotificationController {

    private final NotificationService notificationService;

    // Notification 엔티티를 NotificationDTO로 변환하는 메소드
    private NotificationDTO convertToDto(Notification notification) {
        return NotificationDTO.builder()
                .notificationId(notification.getNotificationId())
                .orderId(notification.getOrderId())
                .memberEmail(notification.getMemberEmail())
                .totalPrice(notification.getTotalPrice())
                .isRead(notification.isRead())
                .hotelRoomName(notification.getHotelRoomName())
                .build();
    }



    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications() {
        log.info("읽지 않은 알림 조회 API 호출됨 (/unread)");

        // <<<--- 서비스 호출 변경: 상세 정보 포함 DTO 리스트를 가져옴 ---<<<
        List<NotificationDTO> notificationDtos = notificationService.getUnreadNotificationsWithDetails();
        long unreadCount = notificationDtos.size(); // DTO 리스트 크기로 카운트

        Map<String, Object> response = new HashMap<>();
        response.put("count", unreadCount);
        response.put("notifications", notificationDtos); // <<<--- 변환 과정 없이 바로 DTO 리스트 반환 ---<<<

        log.info("읽지 않은 알림 {}건 상세 정보 조회 완료", unreadCount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/read")
    public ResponseEntity<Map<String, Object>> markNotificationsAsRead(@RequestBody List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "알림 ID 목록이 비어있습니다."));
        }
        log.info("알림 읽음 처리 API 호출됨. IDs: {}", notificationIds);
        int updatedCount = notificationService.markNotificationsAsRead(notificationIds);

        Map<String, Object> response = new HashMap<>();
        response.put("message", updatedCount + "개의 알림이 읽음 처리되었습니다.");
        response.put("updatedCount", updatedCount);

        log.info("알림 읽음 처리 완료: {}건", updatedCount);
        return ResponseEntity.ok(response);
    }
}
