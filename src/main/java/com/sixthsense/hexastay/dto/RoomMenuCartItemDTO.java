package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import jakarta.persistence.*;
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

    private Long roomMenuCartItemNum;  // 장바구니 항목의 고유 ID

    private RoomMenuCart roomMenuCart; // 카트를 참조

    private Integer roomMenuCartItemAmount; // 장바구니의 수량

    private RoomMenu roomMenu; // 메뉴를 참조
}
