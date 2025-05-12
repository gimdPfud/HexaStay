package com.sixthsense.hexastay.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile; // MultipartFile은 요청 시에만 사용하는 것이 일반적입니다.
// 응답 DTO에는 파일 경로(String)를 담는 것이 좋습니다.
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
// AllArgsConstructor는 모든 필드를 받는 생성자를 만듭니다. roomMenuId를 포함하도록 하거나,
// JPQL에서 사용할 특정 생성자를 명시적으로 만들어주는 것이 좋습니다.
public class RoomMenuCartDetailDTO {

    private Long roomMenuCartDetailNum; // 장바구니 아이템의 PK (RoomMenuCartItem의 PK)
    private String roomMenuCartDetailMenuItemName;
    private Integer roomMenuCartDetailMenuItemPrice; // ★ JPQL에서 상품(RoomMenu)의 개당 가격을 여기에 매핑하는지 확인 필요
    private Integer roomMenuCartDetailMenuItemAmount;
    private String roomMenuImageMeta; // 이미지 경로 (String)
    // private MultipartFile roomMenuImage; // 이 필드는 응답 DTO에서 일반적으로 사용하지 않습니다.

    private String roomMenuSelectOptionName;
    private Integer roomMenuSelectOptionPrice; // 이 아이템에 적용된 총 옵션 추가금액

    private Integer roomMenuCartItemPrice; // ★ 이 아이템의 최종 가격 ((상품단가*수량) + 총옵션금액)

    private List<RoomMenuCartItemOptionDTO> optionList;

    // ★★★ (추가) 실제 상품(RoomMenu)의 ID를 담을 필드 ★★★
    private Long roomMenuId;

    // JPQL에서 new com.sixthsense.hexastay.dto.RoomMenuCartDetailDTO(...) 형태로 사용할 생성자
    // 순서와 타입이 JPQL의 SELECT 절과 정확히 일치해야 합니다.
    public RoomMenuCartDetailDTO(Long roomMenuCartDetailNum, String roomMenuCartDetailMenuItemName,
                                 Integer roomMenuCartDetailMenuItemPrice, Integer roomMenuCartDetailMenuItemAmount,
                                 String roomMenuSelectOptionName, Integer roomMenuSelectOptionPrice,
                                 String roomMenuImageMeta,
                                 Long roomMenuId, // ★ 추가된 상품 ID 파라미터
                                 Integer roomMenuCartItemPrice) { // ★ 장바구니 아이템 최종 가격
        this.roomMenuCartDetailNum = roomMenuCartDetailNum;
        this.roomMenuCartDetailMenuItemName = roomMenuCartDetailMenuItemName;
        this.roomMenuCartDetailMenuItemPrice = roomMenuCartDetailMenuItemPrice;
        this.roomMenuCartDetailMenuItemAmount = roomMenuCartDetailMenuItemAmount;
        this.roomMenuSelectOptionName = roomMenuSelectOptionName;
        this.roomMenuSelectOptionPrice = roomMenuSelectOptionPrice;
        this.roomMenuImageMeta = roomMenuImageMeta;
        this.roomMenuId = roomMenuId; // ★ 여기에 실제 상품 ID가 매핑되어야 함
        this.roomMenuCartItemPrice = roomMenuCartItemPrice;
    }
    // Lombok의 @AllArgsConstructor를 사용한다면, 모든 필드를 포함하는 생성자가 자동으로 만들어지므로
    // 위와 같은 별도 생성자는 없어도 되지만, JPQL에서 특정 순서로 필드를 지정한다면 해당 순서에 맞는 생성자가 필요합니다.
    // 또는 setter를 통해 값을 채울 수도 있습니다 (JPQL에서 DTO 직접 생성 시에는 생성자 방식이 일반적).
}