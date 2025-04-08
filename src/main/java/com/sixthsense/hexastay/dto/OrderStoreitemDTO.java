/********************************************
 * 클래스명 : OrderStoreitemDTO
 * 기능 : OrderStoreitemDTO
 * 작성자 : 김예령
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
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
public class OrderStoreitemDTO {

    private Long orderstoreitemNum; //pk

    private Long orderstoreitemAmount;      //주문한 메뉴 양

    private Long orderstoreitemPrice;       //주문한 메뉴 가격

    private Long orderstoreitemTotalPrice;  //주문한 메뉴들의 총가격

    //외부업체 상품판매일
    private LocalDateTime createDate;

    //외부업체 상품 수정일자
    private LocalDateTime modifyDate;

    //*********************************
    //외부업체 상품 가져올 PK - Storemenu
    private Long storemenuNum;

    //**********************************
    //멤버 정보를 가져올 PK - orderstoreNum
    private Long orderstoreNum;
}
