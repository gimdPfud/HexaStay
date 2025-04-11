package com.sixthsense.hexastay.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class RoomMenuOrderDTO {

    private  Long roomMenuOrderNum; // pk

    @NotNull(message = "수량은 필수 입력값입니다.")
    @Positive(message = "1개 이상 주문해야합니다.")
    private  int roomMenuOrderAmount; // 주문의 수량

}
