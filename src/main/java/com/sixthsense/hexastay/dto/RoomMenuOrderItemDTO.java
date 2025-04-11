package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.RoomMenuOrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class RoomMenuOrderItemDTO {

    //주문아이템
    private String roomMenuOrderItemNum;

    private Integer roomMenuOrderItemAmount; //주문수량

    private Integer roomMenuOrderItemPrice; //주문금액



    public RoomMenuOrderItemDTO(RoomMenuOrderItem orderItem) {
        this.roomMenuOrderItemNum = orderItem.getRoomMenu().getRoomMenuName();
        this.roomMenuOrderItemAmount = orderItem.getRoomMenuOrderAmount();
        this.roomMenuOrderItemPrice = orderItem.getRoomMenuOrderPrice();
    }

}
