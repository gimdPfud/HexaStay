package com.sixthsense.hexastay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

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

    private Long roomMenuCartItemNum;  // pk

    private Long roomMenuNum; // 상품(RoomMenu)의 ID

    @NotNull(message = "수량은 필수 입력값입니다.")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private Integer roomMenuCartItemAmount;

    private Integer roomMenuCartItemCount; // 카트에 담긴 메뉴 아이템의 갯수

    private String roomMenuSelectOptionName;  // 옵션의 이름.

    private Integer roomMenuSelectOptionPrice; //옵션의 가격

    private Integer roomMenuCartItemPrice; // 상품의 기본 가격

    private List<RoomMenuCartItemOptionDTO> selectedOptions;




}
