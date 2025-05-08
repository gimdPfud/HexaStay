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

    private Long orderstoreNum;         //pk
    private String orderstoreStatus;    //주문상태
    private String orderstoreMessage;   //요청사항
    private LocalDateTime createDate;   //베이스엔티티-생성날짜
    private LocalDateTime modifyDate;   //베이스엔티티-수정날짜

    //**********************************
    //멤버 정보를 가져올 PK - room
    private Long storeNum;
    private Long roomNum;

    private List<OrderstoreitemDTO> orderstoreitemDTOList = new ArrayList<>();
}
