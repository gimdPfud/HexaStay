/********************************************
 * 클래스명 : OrderStoreDTO
 * 기능 : OrderStoreDTO
 * 작성자 : 김부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 날짜 필드 이름 수정 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStoreDTO {


    //외부업체 판매수량
    private Long orderStoreAmount;

    //외부업체 상품가격
    private Long orderStorePrice;

    //외부업체 상품판매일
    private LocalDateTime createDate;

    //외부업체 상품 수정일자
    private LocalDateTime modifyDate;

    //외부업체 상품 총금액
    private Long orderStoreTotalPrice;

    //외부업체 결재 (이체/카드/현금 사용여부)
    private String orderStorePay;

    //*********************************
    //외부업체 상품 가져올 PK - Storemenu
    private Long storeMenuNum;

    //**********************************
    //멤버 정보를 가져올 PK - member
    private Long memberNum;



}
