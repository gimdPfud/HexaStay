package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.dto.NotificationPayloadDTO;
import com.sixthsense.hexastay.entity.RoomMenuOrder;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import com.sixthsense.hexastay.repository.RoomMenuOrderRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/roommenu/admin/orderalert")
@RequiredArgsConstructor
@Log4j2

public class RoomMenuAdminOrderActionController {

    private static final Logger log = LoggerFactory.getLogger(RoomMenuAdminOrderActionController.class);
    // private final RoomMenuOrderService roomMenuOrderService; // 서비스 계층 대신 Repository 직접 사용
    private final RoomMenuOrderRepository roomMenuOrderRepository; // Repository 주입

    @Autowired // SimpMessagingTemplate은 @Autowired로 주입 (또는 생성자 주입)
    private SimpMessagingTemplate messagingTemplate;

    // 주문 접수
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
                    updatedOrderEntity = roomMenuOrderRepository.save(order); // DB에 저장하고 변경된 엔티티 받기
                    log.info("주문 ID [{}] 상태 ACCEPT로 DB 저장 완료.", orderId);
                } else {
                    log.warn("주문 ID [{}]를 찾을 수 없어 상태 변경 실패.", orderId);
                    // 여기서 특정 주문 실패 시 전체를 롤백할지, 아니면 개별 처리할지 정책에 따라 다름
                    // 여기서는 일단 다음 주문으로 넘어가도록 continue 사용하지 않고,
                    // 에러 응답 없이 로그만 남기고 알림도 보내지 않음 (또는 에러 메시지 리스트에 추가 후 마지막에 반환)
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
                // 개별 주문 처리 중 예외 발생 시 로그 기록 (DB 롤백은 @Transactional에 의해 관리됨)
                log.error("주문 ID [{}] '접수' 처리 중 예외 발생: {}", orderId, e.getMessage(), e);
                // 여기서도 전체 실패로 처리할지 개별 실패로 할지 정책 필요.
                // 전체 중 하나라도 실패 시 에러를 반환하고 싶다면 바로 return 할 수 있음.
                // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "주문 ID " + orderId + " 처리 중 오류 발생: " + e.getMessage()));
            }
        }
        // 모든 주문 처리가 (성공적으로 또는 일부 실패 후 계속 진행하여) 완료된 후 최종 응답
        return ResponseEntity.ok(Map.of("message", "선택된 주문에 대한 '접수' 처리가 시도되었습니다."));
    }

    // 주문 완료
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
                    log.warn("주문 ID [{}]를 찾을 수 없어 상태 변경 실패.", orderId);
                    continue;
                }

                if (updatedOrderEntity != null && updatedOrderEntity.getMember() != null) {
                    String userPrincipalName = updatedOrderEntity.getMember().getMemberEmail();
                    NotificationPayloadDTO payload = NotificationPayloadDTO.builder()
                            .type("ORDER_STATUS_UPDATE")
                            .notificationId(System.currentTimeMillis())
                            .orderId(updatedOrderEntity.getRoomMenuOrderNum())
                            .title("주문 완료")
                            .message("고객님의 주문(번호: " + updatedOrderEntity.getRoomMenuOrderNum() + ")이 완료되었습니다. 맛있게 드세요!")
                            .status(RoomMenuOrderStatus.COMPLETE.name())
                            .createDate(LocalDateTime.now().toString())
                            .isRead(false)
                            .build();
                    messagingTemplate.convertAndSendToUser(userPrincipalName, "/queue/order-status", payload);
                    log.info("주문 ID [{}] 사용자 [{}]에게 '완료' 알림 전송.", orderId, userPrincipalName);
                }
            } catch (Exception e) {
                log.error("주문 ID [{}] '완료' 처리 중 예외 발생: {}", orderId, e.getMessage(), e);
            }
        }
        return ResponseEntity.ok(Map.of("message", "선택된 주문에 대한 '완료' 처리가 시도되었습니다."));
    }

    // 주문 취소
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
                    log.warn("주문 ID [{}]를 찾을 수 없어 상태 변경 실패.", orderId);
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
                log.error("주문 ID [{}] '취소' 처리 중 예외 발생: {}", orderId, e.getMessage(), e);
            }
        }
        return ResponseEntity.ok(Map.of("message", "선택된 주문에 대한 '취소' 처리가 시도되었습니다."));
    }
}
