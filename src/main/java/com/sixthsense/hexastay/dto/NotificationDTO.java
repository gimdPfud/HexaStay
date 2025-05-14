package com.sixthsense.hexastay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NotificationDTO {

    private Long notificationId; // pk 값

    private Long orderId; // 관련 주문 ID

    private String memberEmail;

    private Integer totalPrice; //토탈 금액

    private boolean isRead = false; // 기본값 false


    private String hotelRoomName;

    private LocalDateTime createDate; // 시간정보 추가



}
