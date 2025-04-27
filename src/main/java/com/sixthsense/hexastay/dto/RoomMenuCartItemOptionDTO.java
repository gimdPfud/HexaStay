package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder

public class RoomMenuCartItemOptionDTO {

    private Long rooMenuCartItemOptionNum;

    private String roomMenuCartItemOptionName;
    private Integer roomMenuCartItemOptionPrice;
    private Integer roomMenuCartItemOptionAmount;

    private RoomMenuCartItem roomMenuCartItem;

    public RoomMenuCartItemOptionDTO(String name, Integer price, Integer amount) {
        this.roomMenuCartItemOptionName = name;
        this.roomMenuCartItemOptionPrice = price;
        this.roomMenuCartItemOptionAmount = amount;
    }

}
