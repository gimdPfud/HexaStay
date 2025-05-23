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

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Storemenu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storemenuNum;

    //룸서비스외부 업체 상품이름
    @Column(nullable = false, length = 20)
    private String storemenuName;

    //룸서비스외부 업체상품 가격
    @Column(nullable = false)
    private Integer storemenuPrice;

    //룸서비스외부 업체상품 설명
    @Column(length = 500)
    private String storemenuContent;

    //룸서비스외부 업체상품 카테고리
    @Column(nullable = false)
    private String storemenuCategory;

    //룸서비스외부 업체상품 서비스 활성화 여부
    private String storemenuStatus;

    //참조 테이블 - Store 테이블 Pk 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeNum")
    private Store store;

    private String storemenuImgMeta; //메뉴 대표 사진

    @ToString.Exclude
    @OneToMany(mappedBy = "storemenu", cascade = CascadeType.ALL)
    private List<StoremenuOption> storemenuOptionList = new ArrayList<>();
}