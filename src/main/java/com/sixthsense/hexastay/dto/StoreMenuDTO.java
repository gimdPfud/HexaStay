/***********************************************
 * 클래스명 : OrderStoreDTO
 * 기능 : OrderStore DTO
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
public class StoreMenuDTO {

    private Long storeServiceNum;

    //룸서비스외부 업체 상품이름
    private String storeServiceName;

    //룸서비스외부 업체상품 가격
    private Long storeServicePrice;

    //룸서비스외부 업체상품 설명
    private String storeServiceContent;

    //룸서비스외부 업체상품 카테고리
    private String storeServiceCategory;

    //룸서비스외부 업체상품 서비스 활성화 여부
    private String storeServiceStatus;

    //룸서비스외부 등록일
    private LocalDateTime createDate;

    //룸서비스외부 수정일
    private LocalDateTime modifyDate;

    //참조 테이블 - Store 테이블 Pk 참조
    private Long storeNum;


}