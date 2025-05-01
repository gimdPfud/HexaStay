package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
