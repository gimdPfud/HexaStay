package com.sixthsense.hexastay.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoomMenuCartDetailDTO {

    private Long roomMenuCartDetailNum; //장바구니 상품 아이디

    private String roomMenuCartDetailMenuItemName;  // 디테일 상품 메뉴 이름

    private Integer roomMenuCartDetailMenuItemPrice; //상품금액

    private Integer roomMenuCartDetailMenuItemAmount;  //상품수량

}