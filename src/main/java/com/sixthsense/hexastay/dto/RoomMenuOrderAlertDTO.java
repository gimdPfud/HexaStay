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

}
