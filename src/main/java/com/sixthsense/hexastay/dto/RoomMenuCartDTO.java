package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.RoomMenuCart;
import lombok.*;
import java.util.List;

/***************************************************
 *
 * 클래스명 : RoomMenuCartDTO
 * 기능 : 룸서비스 장바구니의 데이터를 전송하기 위한 DTO
 *        장바구니 정보와 그에 관련된 항목들을 포함
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
public class RoomMenuCartDTO {

    private Long roomMenuCartNum; // 장바구니의 고유 번호
    private Integer totalPrice;   // 장바구니의 총 가격
    private Long memberNum;       // 사용자 ID (연관된 회원의 ID)

    private List<RoomMenuCartItemDTO> roomMenuCartItems; // 장바구니 항목 리스트

    public RoomMenuCartDTO(RoomMenuCart cart) {
        this.roomMenuCartNum = cart.getRoomMenuCartNum();
        this.totalPrice = cart.getTotalPrice();
        this.memberNum = cart.getMember().getMemberNum();
        this.roomMenuCartItems = cart.getRoomMenuCartItems().stream()
                .map(roomMenuCartItem -> new RoomMenuCartItemDTO(roomMenuCartItem))
                .toList();

    }
}
