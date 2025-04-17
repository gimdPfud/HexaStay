package com.sixthsense.hexastay.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/***********************************************
 * 클래스명 : RoomMenuOrderItemDTO
 * 기능 : 각 룸 메뉴 주문 아이템의 상세 정보를 담는 DTO 클래스
 * - 주문 아이템 고유 번호, 주문 수량, 주문 시의 금액 포함
 * - 사용자의 특별 요청 사항을 저장하는 필드 포함
 * - 주문한 룸 메뉴의 이름을 저장하는 필드 포함
 * 작성자 : 자동 생성
 * 작성일 : 2025-04-09
 * 수정일 : -
 * ***********************************************/

@Getter
@Setter
@ToString
@NoArgsConstructor

public class RoomMenuOrderItemDTO {

    //주문아이템
    private String roomMenuOrderItemNum;

    private Integer roomMenuOrderItemAmount; //주문수량

    private Integer roomMenuOrderItemPrice; //주문금액

    private String roomMenuOrderRequestMessage; // 요청사항


    private String roomMenuOrderItemName; // 아이템 이름

}
