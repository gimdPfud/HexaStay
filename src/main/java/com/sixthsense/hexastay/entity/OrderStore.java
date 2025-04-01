/***********************************************
 * 클래스명 : OrderStore
 * 기능 : OrderStore 엔티티
 * 작성자 : 김부환
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
public class OrderStore extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderStoreNum")
    private Long orderStoreNum;

    //외부업체 판매수량
    private Long orderStoreAmount;

    //외부업체 상품가격
    private Long orderStorePrice;

    //외부업체 상품 총금액
    private Long orderStoreTotalPrice;

    //외부업체 결재 (이체/카드/현금 사용여부)
    private String orderStorePay;

    //외부업체 상품 서비스를 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeMenuNum")
    private StoreMenu storeMenu;


    //멤버 정보를 가져올 참조 테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberNum")
    private Member member;






}