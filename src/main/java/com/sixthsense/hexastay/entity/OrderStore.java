/***********************************************
 * 클래스명 : OrderStore
 * 기능 : OrderStore 엔티티
 * 작성자 : 김부환
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
public class OrderStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderStoreNum")
    private Long orderStoreNum;

    //외부업체 판매수량
    private Long orderStoreAmount;

    //외부업체 상품가격
    private Long orderStorePrice;

    //외부업체 상품판매일
    private LocalDateTime orderStoreCreateDate;

    //외부업체 상품 수정일자
    private LocalDateTime orderStoreModifyDate;

    //외부업체 상품 총금액
    private Long orderStoreTotalPrice;

    //외부업체 결재 (이체/카드/현금 사용여부)
    private String orderStorePay;

    //외부업체 상품 서비스를 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serviceUnionNum")
    private ServiceUnion serviceUnion;


    //멤버 정보를 가져올 참조 테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberNum")
    private Member member;






}