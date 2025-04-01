/***********************************************
 * 클래스명 : StoreMenu
 * 기능 : StoreMenu 엔티티
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
public class StoreMenu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storeServiceNum")
    private Long storeServiceNum;

    //룸서비스외부 업체 상품이름
    private String storeServicesName;

    //룸서비스외부 업체상품 가격
    private Long storeServicePrice;

    //룸서비스외부 업체상품 설명
    private String storeServiceContent;

    //룸서비스외부 업체상품 카테고리
    private String storeServiceCategory;

    //룸서비스외부 업체상품 서비스 활성화 여부
    private String storeServiceStatus;

    //참조 테이블 - Store 테이블 Pk 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeNum")
    private Store Store;


}