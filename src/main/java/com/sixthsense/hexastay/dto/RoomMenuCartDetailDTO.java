package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.RoomMenu;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/***************************************************
 *
 * 클래스명   : RoomMenuCartDetailDTO
 * 기능      : 룸 메뉴 장바구니 상세 정보를 담는 DTO 클래스
 * - 장바구니 아이템의 고유 번호, 메뉴 이름, 가격, 수량 등의 정보를 관리
 * - 룸 메뉴 이미지 파일 및 관련 메타데이터를 포함
 * - 특정 필드만을 초기화하는 생성자 제공
 * - RoomMenuDTO 객체를 참조하여 관련된 룸 메뉴 정보에 접근 가능
 * 작성자    : 김윤겸
 * 작성일    : 2025-04-05
 * 수정일    : -
 *
 ****************************************************/

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