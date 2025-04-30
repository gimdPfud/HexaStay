package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RoomMenuOrderAlertDTO {

    private String memberEmail;
    private Integer totalPrice;
    private String roomMenuOrderRequestMessage; // 추가
    private Integer roomMenuOrderAmount;    // 추가

    /* 새로 추가됨 */

    private Long notificationId;
    private Long orderId;
    private java.time.LocalDateTime orderTimestamp;

    private String hotelRoomName; // 호텔의 이름


}
