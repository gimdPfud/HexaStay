/***********************************************
 * 클래스명 : ServiceUnion
 * 기능 : ServiceUnion 엔티티
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
public class ServiceUnion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceUnionNum")
    private Long serviceUnionNum;

    //룸서비스외부 업체 상품이름
    private String serviceUnionsName;

    //룸서비스외부 업체상품 가격
    private Long serviceUnionPrice;

    //룸서비스외부 업체상품 설명
    private String serviceUnionContent;

    //룸서비스외부 업체상품 카테고리
    private String serviceUnionCategory;

    //룸서비스외부 업체상품 서비스 활성화 여부
    private String serviceUnionStatus;

    //참조 테이블 - Unions 테이블 Pk 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unionsNum")
    private Unions Unions;


}