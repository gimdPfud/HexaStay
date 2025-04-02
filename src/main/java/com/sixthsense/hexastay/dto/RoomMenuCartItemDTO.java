package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import lombok.*;

/***************************************************
 *
 * 클래스명 : RoomMenuCartItemDTO
 * 기능 : 장바구니 항목에 대한 DTO
 *        장바구니에 담긴 상품의 정보와 수량 등을 담고 있다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-02
 * 수정일 : -
 *
 ****************************************************/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMenuCartItemDTO {

    private Long roomMenuCartItemNum;  // 장바구니 항목 고유 번호
    private Integer roomMenuCartItemAmount;  // 상품 수량
    private Integer roomMenuCartItemPrice;   // 상품 가격 (수량 * 가격)

    private Long roomMenuNum;   // 연관된 메뉴(상품)의 ID
    private String roomMenuName; // 메뉴 이름

    // RoomMenuCartItem을 RoomMenuCartItemDTO로 변환하는 생성자
    public RoomMenuCartItemDTO(RoomMenuCartItem item) {
        this.roomMenuCartItemNum = item.getRoomMenuCartItemNum();
        this.roomMenuCartItemAmount = item.getRoomMenuCartItemAmount();
        this.roomMenuCartItemPrice = item.getRoomMenuCartItemPrice();
        this.roomMenuNum = item.getRoomMenu().getRoomMenuNum();  // RoomMenuCartItem에서 연관된 RoomMenu 객체의 번호
        this.roomMenuName = item.getRoomMenu().getRoomMenuName();  // RoomMenu의 이름
    }
}
