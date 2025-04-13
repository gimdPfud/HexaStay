package com.sixthsense.hexastay.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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

    private MultipartFile roomMenuImage; // 룸 메뉴 이미지

    private String roomMenuImageMeta; //룸 메뉴 사진 정보

    private String originalImageMeta; // 기존 이미지의 경로

    public RoomMenuCartDetailDTO(Long roomMenuCartItemNum, String roomMenuCartDetailMenuItemName,
                                 Integer roomMenuCartDetailMenuItemPrice, Integer roomMenuCartDetailMenuItemAmount,
                                 String roomMenuImageMeta) {
        this.roomMenuCartDetailNum = roomMenuCartItemNum;
        this.roomMenuCartDetailMenuItemName = roomMenuCartDetailMenuItemName;
        this.roomMenuCartDetailMenuItemPrice = roomMenuCartDetailMenuItemPrice;
        this.roomMenuCartDetailMenuItemAmount = roomMenuCartDetailMenuItemAmount;
        this.roomMenuImageMeta = roomMenuImageMeta;
    }
        // DTO를 참조
        private RoomMenuDTO roomMenuDTO;

    }