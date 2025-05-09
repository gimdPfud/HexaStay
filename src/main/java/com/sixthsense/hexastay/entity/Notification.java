package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**************************************************
 * 클래스명 : Notification
 * 기능   : 알림 정보를 나타내는 엔티티 클래스입니다.
 * 시스템에서 발생하는 주요 이벤트(예: 주문 상태 변경)를 사용자에게 알리는 데 사용됩니다.
 * Lombok 어노테이션을 사용하여 getter, setter, builder 등을 간편하게 생성합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-30
 * 수정일 : 2025-05-09
 * 주요 필드 : notificationId (PK), orderId, memberEmail, totalPrice, isRead, hotelRoomName
 **************************************************/

@Entity
@Getter
@Setter // Setter는 필요에 따라 추가 (isRead 업데이트 등)
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificationId")
    private Long notificationId; // pk 값

    @Column(name = "orderId", nullable = false)
    private Long orderId; // 관련 주문 ID

    @Column(name = "memberEmail") // 메시지 표시에 사용
    private String memberEmail;

    @Column(name = "totalPrice") // 메시지 표시에 사용
    private Integer totalPrice; //토탈 금액

    @Column(name = "isRead", nullable = false)
    private boolean isRead = false; // 기본값 false

    private String hotelRoomName;



}
