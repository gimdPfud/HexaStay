package com.sixthsense.hexastay.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class RoomMenuCartDetailDTO {

    private Long roomMenuCartDetailNum;
    private String roomMenuCartDetailMenuItemName;
    private Integer roomMenuCartDetailMenuItemPrice;
    private Integer roomMenuCartDetailMenuItemAmount;
    private String roomMenuImageMeta;
    // private MultipartFile roomMenuImage; // 이 필드는 응답 DTO에서 일반적으로 사용하지 않습니다.

    private String roomMenuSelectOptionName;
    private Integer roomMenuSelectOptionPrice;

    private Integer roomMenuCartItemPrice;

    private List<RoomMenuCartItemOptionDTO> optionList;

    private Integer availableStock; // 남은 재고를 확인하는 필드

    // ★★★ (추가) 실제 상품(RoomMenu)의 ID를 담을 필드 ★★★
    private Long roomMenuId;


    // JPQL 등에서 사용할 생성자 (availableStock 파라미터 추가)
    public RoomMenuCartDetailDTO(Long roomMenuCartDetailNum, String roomMenuCartDetailMenuItemName,
                                 Integer roomMenuCartDetailMenuItemPrice, Integer roomMenuCartDetailMenuItemAmount, // 장바구니 수량
                                 String roomMenuSelectOptionName, Integer roomMenuSelectOptionPrice,
                                 String roomMenuImageMeta,
                                 Long roomMenuId,
                                 Integer roomMenuCartItemPrice,
                                 Integer availableStock) { // ★ 추가된 재고 파라미터
        this.roomMenuCartDetailNum = roomMenuCartDetailNum;
        this.roomMenuCartDetailMenuItemName = roomMenuCartDetailMenuItemName;
        this.roomMenuCartDetailMenuItemPrice = roomMenuCartDetailMenuItemPrice;
        this.roomMenuCartDetailMenuItemAmount = roomMenuCartDetailMenuItemAmount; // 여전히 장바구니 수량
        this.roomMenuSelectOptionName = roomMenuSelectOptionName;
        this.roomMenuSelectOptionPrice = roomMenuSelectOptionPrice;
        this.roomMenuImageMeta = roomMenuImageMeta;
        this.roomMenuId = roomMenuId;
        this.roomMenuCartItemPrice = roomMenuCartItemPrice;
        this.availableStock = availableStock; // ★ 새로 추가된 필드 초기화
    }

}