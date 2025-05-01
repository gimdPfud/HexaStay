package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.NotificationDTO;
import com.sixthsense.hexastay.dto.RoomMenuOrderAlertDTO;
import com.sixthsense.hexastay.entity.Notification;
import com.sixthsense.hexastay.entity.RoomMenuOrder;
import com.sixthsense.hexastay.repository.NotificationRepository;
import com.sixthsense.hexastay.repository.RoomMenuOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional

public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RoomMenuOrderRepository roomMenuOrderRepository;

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

    public List<NotificationDTO> getUnreadNotificationsWithDetails() { // <<<--- 반환 타입을 NotificationDTO로 변경
        log.info("읽지 않은 알림 상세 정보 조회 시작 (using NotificationDTO)");
        List<Notification> notifications = notificationRepository.findByIsReadFalseOrderByCreateDateDesc();
        if (notifications.isEmpty()) {
            log.info("읽지 않은 알림 없음");
            return Collections.emptyList();
        }

        List<NotificationDTO> dtos = new ArrayList<>();
        for (Notification notification : notifications) {
            String hotelRoomName = "객실 정보 없음"; // 기본값

            Optional<RoomMenuOrder> orderOpt = roomMenuOrderRepository.findById(notification.getOrderId());
            if (orderOpt.isPresent()) {
                RoomMenuOrder order = orderOpt.get();
                if (order.getHotelRoom() != null && order.getHotelRoom().getHotelRoomName() != null) {
                    hotelRoomName = order.getHotelRoom().getHotelRoomName();
                } else {
                    log.warn("Notification ID {} (Order ID {})에 연결된 HotelRoom 정보가 없습니다.", notification.getNotificationId(), notification.getOrderId());
                }
            } else {
                log.warn("Notification ID {}에 연결된 주문(Order ID {}) 정보를 찾을 수 없습니다.", notification.getNotificationId(), notification.getOrderId());
            }

            // --- 기존 NotificationDTO를 사용하여 빌드 ---
            dtos.add(NotificationDTO.builder()
                    .notificationId(notification.getNotificationId())
                    .orderId(notification.getOrderId())
                    .memberEmail(notification.getMemberEmail())
                    .totalPrice(notification.getTotalPrice())
                    .isRead(notification.isRead())
                    .createDate(notification.getCreateDate()) // <<<--- createDate 설정
                    .hotelRoomName(hotelRoomName)             // <<<--- hotelRoomName 설정
                    .build());
        }
        log.info("읽지 않은 알림 상세 정보 {}건 조회 완료 (using NotificationDTO)", dtos.size());
        return dtos; // <<<--- NotificationDTO 리스트 반환
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
