package com.sixthsense.hexastay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder

public class RoomMenuCartItemOptionDTO {

    @Column(name = "roomMenuCartItemOptionNum")
    @JsonProperty("roomMenuOptionNum")
    private Long roomMenuCartItemOptionNum;

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
