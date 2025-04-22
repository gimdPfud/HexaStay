package com.sixthsense.hexastay.dto;

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

}
