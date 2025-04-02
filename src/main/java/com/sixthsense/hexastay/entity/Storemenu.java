/***********************************************
 * 클래스명 : Storemenu
 * 기능 : Storemenu 엔티티
 * 작성자 : 김부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-04-01 이름 변경 : 김예령
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
public class Storemenu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storemenuNum;

    //룸서비스외부 업체 상품이름
    private String storemenuName;

    //룸서비스외부 업체상품 가격
    private Long storemenuPrice;

    //룸서비스외부 업체상품 설명
    private String storemenuContent;

    //룸서비스외부 업체상품 카테고리
    private String storemenuCategory;

    //룸서비스외부 업체상품 서비스 활성화 여부
    private String storemenuStatus;

    //참조 테이블 - Store 테이블 Pk 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeNum")
    private Store store;


}