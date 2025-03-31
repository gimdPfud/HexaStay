/***********************************************
 * 클래스명 : OrderService
 * 기능 : OrderService 엔티티
 * 작성자 : 깁부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderService {
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

    //룸서비스 발생일자
    private LocalDateTime orderServiceCreateDate;

    //룸서비스 수정일자
    private LocalDateTime orderServiceModifyDate;

    //*****참조테이블*************
    //룸서비스 참조(RoomService)
    @ManyToOne
    @JoinColumn(name = "roomServiceNum")
    private RoomService roomService;


}