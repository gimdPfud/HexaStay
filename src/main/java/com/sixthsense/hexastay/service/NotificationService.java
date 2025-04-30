package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuOrderAlertDTO;
import com.sixthsense.hexastay.entity.Notification;
import com.sixthsense.hexastay.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional

public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createAndSaveNewOrderNotification(Long orderId, RoomMenuOrderAlertDTO alertInfo) {
        log.info("새로운 주문 알림 생성 시도. Order ID: {}", orderId);
        // 필요 시 중복 알림 생성 방지 로직 추가 (예: existsByOrderId)

        Notification notification = Notification.builder()
                .orderId(orderId)
                .memberEmail(alertInfo.getMemberEmail())
                .totalPrice(alertInfo.getTotalPrice())
                .isRead(false) // 기본값 false 명시
                // .message(...) // 필요 시 메시지 생성
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("주문 알림 저장 완료. Notification ID: {}", savedNotification.getNotificationId());
        return savedNotification;
    }


    public long getUnreadNotificationCount() {
        return notificationRepository.countByIsReadFalse();
    }


    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalseOrderByCreateDateDesc();
    }


    public int markNotificationsAsRead(List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return 0;
        }
        log.info("알림 읽음 처리 시도. IDs: {}", notificationIds);
        int updatedCount = notificationRepository.markAsReadByIds(notificationIds);
        log.info("{}개의 알림을 읽음 처리했습니다.", updatedCount);
        return updatedCount;
    }
}
