/***********************************************
 * 클래스명 : Union
 * 기능 : Union 엔티티
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
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storeNum")
    private Long storeNum;

    //외부 업체 이름
    private String storeName;

    //외부업체 매장번호
    private String storePhone;

    //외부 업체주소
    //  문자열 주소
    private String storeAddress;
    //  경도 x
    private Double storeLongitude;
    //  위도 y 카카오맵에서는 위도,경도 순으로 받음
    private Double storeLatitude;
    private Double storeWtmX;
    private Double storeWtmY;

    //외부 업체 대표자명
    private String storeCeoName;

    //외부 업체 패스워드
    private String storePassword;

    //추가 : 활성화 상태
    private String storeStatus;

    //추가 : 카테고리 ( 한식 중식 일식 아시안 양식 패스트푸드 카페  중 1택.)
    private String storeCategory;

    private String storeProfileMeta; //가게 대표 사진

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_num")
    private Company company;
}