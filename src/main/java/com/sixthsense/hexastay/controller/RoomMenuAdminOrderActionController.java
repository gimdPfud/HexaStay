package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.NotificationPayloadDTO;
import com.sixthsense.hexastay.entity.RoomMenuOrder;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import com.sixthsense.hexastay.service.RoomMenuOrderService;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/roommenu/admin/orderalert")
@RequiredArgsConstructor
@Log4j2

public class RoomMenuAdminOrderActionController {

    private static final Logger log = LoggerFactory.getLogger(RoomMenuAdminOrderActionController.class);
    private final RoomMenuOrderService roomMenuOrderService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 주문 접수시의 알람
    @PostMapping("/accept-orders")
    public ResponseEntity<?> acceptOrders(@RequestBody List<Long> orderIds, Principal principal) {
        log.info("관리자 [{}] 주문 접수 API 호출. 대상 주문 ID: {}", principal.getName(), orderIds);

        for (Long orderId : orderIds) {
            try {
                RoomMenuOrder updatedOrder = roomMenuOrderService.acceptOrder(orderId); // 서비스 호출

                if (updatedOrder != null && updatedOrder.getMember() != null) {
                    String userPrincipalName = updatedOrder.getMember().getMemberEmail(); // 알림 받을 사용자 이메일

                    NotificationPayloadDTO payload = NotificationPayloadDTO.builder()
                            .type("ORDER_STATUS_UPDATE")
                            .notificationId(System.currentTimeMillis()) // 임시 ID
                            .orderId(updatedOrder.getRoomMenuOrderNum())
                            .title("주문 접수")
                            .message("고객님의 주문(번호: " + updatedOrder.getRoomMenuOrderNum() + ")이 매장에서 확인되어 준비를 시작합니다.")
                            .status(RoomMenuOrderStatus.ACCEPT.name())
                            .createDate(LocalDateTime.now().toString())
                            .isRead(false)
                            .build();

                    messagingTemplate.convertAndSendToUser(userPrincipalName, "/queue/order-status", payload);
                    log.info("주문 ID [{}] 접수 완료. 사용자 [{}] 알림 전송.", orderId, userPrincipalName);
                }
            } catch (Exception e) {
                log.error("주문 ID [{}] 접수 처리 중 예외 발생: {}", orderId, e.getMessage());
                // 개별 주문 실패 시 전체 실패로 처리하지 않고 계속 진행 (필요시 변경)
            }
        }
        return ResponseEntity.ok(Map.of("message", "선택된 주문에 대한 '접수' 처리가 완료되었습니다."));
    }


    // 주문 완료시의 알람
    @PostMapping("/complete-orders")
    public ResponseEntity<?> completeOrders(@RequestBody List<Long> orderIds, Principal principal) {
        log.info("관리자 [{}] 주문 완료 API 호출. 대상 주문 ID: {}", principal.getName(), orderIds);

        for (Long orderId : orderIds) {
            try {
                RoomMenuOrder updatedOrder = roomMenuOrderService.completeOrder(orderId); // 서비스 호출

                if (updatedOrder != null && updatedOrder.getMember() != null) {
                    String userPrincipalName = updatedOrder.getMember().getMemberEmail();

                    NotificationPayloadDTO payload = NotificationPayloadDTO.builder()
                            .type("ORDER_STATUS_UPDATE")
                            .notificationId(System.currentTimeMillis())
                            .orderId(updatedOrder.getRoomMenuOrderNum())
                            .title("주문 완료")
                            .message("고객님의 주문(번호: " + updatedOrder.getRoomMenuOrderNum() + ")이 완료되었습니다. 맛있게 드세요!")
                            .status(RoomMenuOrderStatus.COMPLETE.name())
                            .createDate(LocalDateTime.now().toString())
                            .isRead(false)
                            .build();

                    messagingTemplate.convertAndSendToUser(userPrincipalName, "/queue/order-status", payload);
                    log.info("주문 ID [{}] 완료 처리. 사용자 [{}] 알림 전송.", orderId, userPrincipalName);
                }
            } catch (Exception e) {
                log.error("주문 ID [{}] 완료 처리 중 예외 발생: {}", orderId, e.getMessage());
            }
        }
        return ResponseEntity.ok(Map.of("message", "선택된 주문에 대한 '완료' 처리가 완료되었습니다."));
    }

    @PostMapping("/cancel-orders")
    public ResponseEntity<?> cancelOrders(@RequestBody List<Long> orderIds, Principal principal) {
        log.info("관리자 [{}] 주문 취소 API 호출. 대상 주문 ID: {}", principal.getName(), orderIds);

        for (Long orderId : orderIds) {
            try {
                RoomMenuOrder updatedOrder = roomMenuOrderService.cancelOrderAsAdmin(orderId); // 서비스 호출

                if (updatedOrder != null && updatedOrder.getMember() != null) {
                    String userPrincipalName = updatedOrder.getMember().getMemberEmail();

                    NotificationPayloadDTO payload = NotificationPayloadDTO.builder()
                            .type("ORDER_STATUS_UPDATE")
                            .notificationId(System.currentTimeMillis())
                            .orderId(updatedOrder.getRoomMenuOrderNum())
                            .title("주문 취소")
                            .message("고객님의 주문(번호: " + updatedOrder.getRoomMenuOrderNum() + ")이 취소되었습니다.")
                            .status(RoomMenuOrderStatus.CANCEL.name())
                            .createDate(LocalDateTime.now().toString())
                            .isRead(false)
                            .build();

                    messagingTemplate.convertAndSendToUser(userPrincipalName, "/queue/order-status", payload);
                    log.info("주문 ID [{}] 취소 처리. 사용자 [{}] 알림 전송.", orderId, userPrincipalName);
                }
            } catch (Exception e) {
                log.error("주문 ID [{}] 취소 처리 중 예외 발생: {}", orderId, e.getMessage());
            }
        }
        return ResponseEntity.ok(Map.of("message", "선택된 주문에 대한 '취소' 처리가 완료되었습니다."));
    }
}
