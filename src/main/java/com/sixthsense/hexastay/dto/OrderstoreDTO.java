/********************************************
 * 클래스명 : OrderstoreDTO
 * 기능 : 스토어 매출 용 DTO
 * 작성자 : 김예령
 * 작성일 : 2025-04-09
 * 수정 : 2025-04-17 김예령:요청사항추가
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderstoreDTO {

    private Long orderstoreNum;

    //외부업체 상품판매일
    private LocalDateTime createDate;

    //외부업체 상품 수정일자
    private LocalDateTime modifyDate;

    //외부업체 결재 (이체/카드/현금 사용여부)
    private String orderstorePay;

    private String orderstoreStatus; // 주문 상태. alive, cancel ?
    private String orderstoreMessage;//주문 요처사항


    //**********************************
    //멤버 정보를 가져올 PK - room
    private Long roomNum;

    private List<OrderstoreitemDTO> orderstoreitemDTOList = new ArrayList<>();
}
