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

    private Integer roomMenuCartItemAmount; // 장바구니 수량

    private Integer roomMenuCartItemPrice; // 항목 가격 (RoomMenu에서 가져올 수 있음)

    private RoomMenuCart roomMenuCart; // 카트를 참조

    private RoomMenu roomMenu; // 룸메뉴서비스를 참조, 해당 항목의 상품
}
