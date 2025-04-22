/***********************************************
 * 클래스명 : Orderstore
 * 기능 : Orderstore 엔티티
 * 작성자 : 김부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-04-17 주문 요청사항 추가
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Orderstore extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderstoreNum")
    private Long orderstoreNum;

    //외부업체 결재 (이체/카드/현금 사용여부)
    private String orderstorePay;

    private String orderstoreStatus; // 주문 상태. alive, cancel, end ?

    @Column(length = 500)
    private String orderstoreMessage;//주문 요청사항

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeNum")
    private Store store;
//    //외부업체 상품 서비스를 참조
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_num")
//    private Member member;

    //멤버 참조 대신 room 참조. (room : 호텔방과 사람을 연결하는 중간테이블)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_num")
    private Room room;

    @ToString.Exclude
    @OneToMany(mappedBy = "orderstore", cascade = CascadeType.ALL)
    private List<Orderstoreitem> orderstoreitemList = new ArrayList<>();
}