/***********************************************
 * 클래스명 : OrderService
 * 기능 : OrderService 엔티티
 * 작성자 : 깁부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 BaseEntity 추가, 기존 날짜 필드 삭제 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderService extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderServiceNum")
    private Long orderServiceNum;

    //룸서비스 판매수량
    private Long orderServiceAmount;

    //룸서비스 상품가격
    private Long orderServicePrice;

    //룸서비스 총매출
    private Long orderServiceTotalPrice;

    //룸서비스 결재
    private String orderServicePay;

    //*****참조테이블*************
    //룸서비스 참조(RoomService)
    @ManyToOne
    @JoinColumn(name = "roomServiceNum")
    private RoomService roomService;


}