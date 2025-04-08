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

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderStore extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderStoreNum")
    private Long orderStoreNum;

    //외부업체 결재 (이체/카드/현금 사용여부)
    private String orderStorePay;

    //외부업체 상품 서비스를 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member;

    @OneToMany(mappedBy = "orderStore", cascade = CascadeType.ALL)
    private List<Orderstoreitem> orderstoreitemList
            = new ArrayList<>();
}