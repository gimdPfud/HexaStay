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

    /**************************************************
     * 메소드명 : convertToDto
     * Notification 엔티티 DTO 변환
     * 기능: Notification 엔티티 객체를 NotificationDTO 객체로 변환하여 반환합니다.
     * 알림 관련 데이터를 외부로 전달하기 용이한 형태로 가공합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-05-01
     * 수정일 : -
     **************************************************/

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

    /**************************************************
     * 메소드명 : getUnreadNotifications
     * 읽지 않은 알림 목록 및 개수 조회
     * 기능: 현재 사용자의 읽지 않은 알림 목록과 그 개수를 조회합니다.
     * 각 알림은 상세 정보(주문 ID, 호텔 객실명 등)를 포함한 DTO 형태로 반환됩니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-30
     * 수정일 : -
     **************************************************/

    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications() {
        log.info("읽지 않은 알림 조회 API 호출됨 (/unread)");

        List<NotificationDTO> notificationDtos = notificationService.getUnreadNotificationsWithDetails();
        long unreadCount = notificationDtos.size(); // DTO 리스트 크기로 카운트

        Map<String, Object> response = new HashMap<>();
        response.put("count", unreadCount);
        response.put("notifications", notificationDtos); // <<<--- 변환 과정 없이 바로 DTO 리스트 반환 ---<<<

        return ResponseEntity.ok(response);
    }

    /**************************************************
     * 메소드명 : markNotificationsAsRead
     * 알림 읽음 상태로 변경 처리
     * 기능: 클라이언트로부터 전달받은 알림 ID 목록을 기반으로 해당 알림들의 상태를 '읽음'으로 변경합니다.
     * 처리된 알림의 개수와 성공 메시지를 응답으로 반환합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-30
     * 수정일 : -
     **************************************************/

    @PostMapping("/read")
    public ResponseEntity<Map<String, Object>> markNotificationsAsRead(@RequestBody List<Long> notificationIds) {
        log.info("알람 읽기 컨트롤러 진입");

        if (notificationIds == null || notificationIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "알림 ID 목록이 비어있습니다."));
        }

        int updatedCount = notificationService.markNotificationsAsRead(notificationIds);

        Map<String, Object> response = new HashMap<>();
        response.put("message", updatedCount + "개의 알림이 읽음 처리되었습니다.");
        response.put("updatedCount", updatedCount);

        log.info("알림 읽음 처리 완료: {}건", updatedCount);
        return ResponseEntity.ok(response);
    }
}
