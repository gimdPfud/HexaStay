package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.NotificationPayloadDTO;
import com.sixthsense.hexastay.entity.RoomMenuOrder;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import com.sixthsense.hexastay.repository.RoomMenuOrderRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/roommenu/admin/orderalert")
@RequiredArgsConstructor
@Log4j2

public class RoomMenuAdminOrderActionController {

    private static final Logger log = LoggerFactory.getLogger(RoomMenuAdminOrderActionController.class);
    private final RoomMenuOrderRepository roomMenuOrderRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**************************************************
     * 메소드명 : acceptOrders
     * 룸서비스 주문 접수 처리
     * 기능: 전달받은 주문 ID 목록에 대해 각 주문의 상태를 'ACCEPT'(접수)로 변경하고, 데이터베이스에 저장합니다.
     * 성공 시 해당 주문자에게 실시간으로 주문 접수 알림을 전송합니다. 관리자가 수행합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-05-07
     * 수정일 : -
     **************************************************/

    @PostMapping("/accept-orders")
    public ResponseEntity<?> acceptOrders(@RequestBody List<Long> orderIds, Principal principal) {
        log.info("관리자 [{}] 주문 접수 API 호출 (Repository 직접 사용). 대상 주문 ID: {}", principal.getName(), orderIds);

        for (Long orderId : orderIds) {
            try {
                Optional<RoomMenuOrder> optionalOrder = roomMenuOrderRepository.findById(orderId);
                RoomMenuOrder updatedOrderEntity = null;

                if (optionalOrder.isPresent()) {
                    RoomMenuOrder order = optionalOrder.get();
                    order.setRoomMenuOrderStatus(RoomMenuOrderStatus.ACCEPT);
                    updatedOrderEntity = roomMenuOrderRepository.save(order);
                } else {
                    continue; // 다음 orderId 처리
                }

                // 알림 전송 로직 (updatedOrderEntity가 null이 아니고, Member 정보가 있을 때)
                if (updatedOrderEntity != null && updatedOrderEntity.getMember() != null) {
                    String userPrincipalName = updatedOrderEntity.getMember().getMemberEmail();
                    NotificationPayloadDTO payload = NotificationPayloadDTO.builder()
                            .type("ORDER_STATUS_UPDATE")
                            .notificationId(System.currentTimeMillis())
                            .orderId(updatedOrderEntity.getRoomMenuOrderNum())
                            .title("주문 접수")
                            .message("고객님의 주문(번호: " + updatedOrderEntity.getRoomMenuOrderNum() + ")이 매장에서 확인되어 준비를 시작합니다.")
                            .status(RoomMenuOrderStatus.ACCEPT.name())
                            .createDate(LocalDateTime.now().toString())
                            .isRead(false)
                            .build();
                    messagingTemplate.convertAndSendToUser(userPrincipalName, "/queue/order-status", payload);
                    log.info("주문 ID [{}] 사용자 [{}]에게 '접수' 알림 전송.", orderId, userPrincipalName);
                }
            } catch (Exception e) {
                log.error("주문 ID [{}] '접수' 처리 중 예외 발생: {}", orderId, e.getMessage(), e);
            }
        }
        return ResponseEntity.ok(Map.of("message", "선택된 주문에 대한 '접수' 처리가 시도되었습니다."));
    }

    /**************************************************
     * 메소드명 : completeOrders
     * 룸서비스 주문 완료 처리
     * 기능: 전달받은 주문 ID 목록에 대해 각 주문의 상태를 'COMPLETE'(완료)로 변경하고, 데이터베이스에 저장합니다.
     * 성공 시 해당 주문자에게 실시간으로 주문 완료 알림을 전송합니다. 관리자가 수행합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-05-07
     * 수정일 : -
     **************************************************/

    @PostMapping("/complete-orders")
    public ResponseEntity<?> completeOrders(@RequestBody List<Long> orderIds, Principal principal) {
        log.info("관리자 [{}] 주문 완료 API 호출 (Repository 직접 사용). 대상 주문 ID: {}", principal.getName(), orderIds);

        for (Long orderId : orderIds) {
            try {
                Optional<RoomMenuOrder> optionalOrder = roomMenuOrderRepository.findById(orderId);
                RoomMenuOrder updatedOrderEntity = null;

                if (optionalOrder.isPresent()) {
                    RoomMenuOrder order = optionalOrder.get();
                    order.setRoomMenuOrderStatus(RoomMenuOrderStatus.COMPLETE);
                    updatedOrderEntity = roomMenuOrderRepository.save(order);
                    log.info("주문 ID [{}] 상태 COMPLETE로 DB 저장 완료.", orderId);
                } else {
                    continue;
                }

                if (updatedOrderEntity != null && updatedOrderEntity.getMember() != null) {
                    String userPrincipalName = updatedOrderEntity.getMember().getMemberEmail();
                    NotificationPayloadDTO payload = NotificationPayloadDTO.builder()
                            .type("ORDER_STATUS_UPDATE")
                            .notificationId(System.currentTimeMillis())
                            .orderId(updatedOrderEntity.getRoomMenuOrderNum())
                            .title("주문 완료")
                            .message("고객님의 주문(번호: " + updatedOrderEntity.getRoomMenuOrderNum() + ")이 완료되었습니다. 좋은 하루 되세요!")
                            .status(RoomMenuOrderStatus.COMPLETE.name())
                            .createDate(LocalDateTime.now().toString())
                            .isRead(false)
                            .build();
                    messagingTemplate.convertAndSendToUser(userPrincipalName, "/queue/order-status", payload);
                    log.info("주문 ID [{}] 사용자 [{}]에게 '완료' 알림 전송.", orderId, userPrincipalName);
                }
            } catch (Exception e) {
            }
        }
        return ResponseEntity.ok(Map.of("message", "선택된 주문에 대한 '완료' 처리가 시도되었습니다."));
    }

    /**************************************************
     * 메소드명 : cancelOrders
     * 룸서비스 주문 취소 처리
     * 기능: 전달받은 주문 ID 목록에 대해 각 주문의 상태를 'CANCEL'(취소)로 변경하고, 데이터베이스에 저장합니다.
     * 성공 시 해당 주문자에게 실시간으로 주문 취소 알림을 전송합니다. 관리자가 수행합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-05-07
     * 수정일 : -
     **************************************************/

    @PostMapping("/cancel-orders")
    public ResponseEntity<?> cancelOrders(@RequestBody List<Long> orderIds, Principal principal) {
        log.info("관리자 [{}] 주문 취소 API 호출 (Repository 직접 사용). 대상 주문 ID: {}", principal.getName(), orderIds);

        for (Long orderId : orderIds) {
            try {
                Optional<RoomMenuOrder> optionalOrder = roomMenuOrderRepository.findById(orderId);
                RoomMenuOrder updatedOrderEntity = null;

                if (optionalOrder.isPresent()) {
                    RoomMenuOrder order = optionalOrder.get();
                    order.setRoomMenuOrderStatus(RoomMenuOrderStatus.CANCEL);
                    updatedOrderEntity = roomMenuOrderRepository.save(order);
                    log.info("주문 ID [{}] 상태 CANCEL로 DB 저장 완료.", orderId);
                } else {
                    continue;
                }

                if (updatedOrderEntity != null && updatedOrderEntity.getMember() != null) {
                    String userPrincipalName = updatedOrderEntity.getMember().getMemberEmail();
                    NotificationPayloadDTO payload = NotificationPayloadDTO.builder()
                            .type("ORDER_STATUS_UPDATE")
                            .notificationId(System.currentTimeMillis())
                            .orderId(updatedOrderEntity.getRoomMenuOrderNum())
                            .title("주문 취소")
                            .message("고객님의 주문(번호: " + updatedOrderEntity.getRoomMenuOrderNum() + ")이 취소되었습니다.")
                            .status(RoomMenuOrderStatus.CANCEL.name())
                            .createDate(LocalDateTime.now().toString())
                            .isRead(false)
                            .build();
                    messagingTemplate.convertAndSendToUser(userPrincipalName, "/queue/order-status", payload);
                    log.info("주문 ID [{}] 사용자 [{}]에게 '취소' 알림 전송.", orderId, userPrincipalName);
                }
            } catch (Exception e) {

            }
        }
        return ResponseEntity.ok(Map.of("message", "선택된 주문에 대한 '취소' 처리가 시도되었습니다."));
    }
}
