package com.sixthsense.hexastay.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NotificationPayloadDTO {

    private String type; // 알림 타입 (예: "ORDER_STATUS_UPDATE")
    private Long notificationId; // 알림 고유 ID (DB에 저장한다면 DB의 PK)
    private Long orderId;        // 관련된 주문 ID
    private String title;        // 알림 제목 (예: "주문 접수 완료")
    private String message;      // 사용자에게 보여줄 상세 메시지
    private String status;       // 변경된 주문 상태 (예: "ACCEPT", "COMPLETE")
    private String createDate;   // 알림 생성 시간 (ISO 문자열 형태)
    private boolean isRead;      // 읽음 여부 (초기값은 false)
}
