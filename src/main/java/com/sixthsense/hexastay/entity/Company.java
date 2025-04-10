/***********************************************
 * 클래스명 : companyDTO
 * 기능 : companyDTO 엔티티
 * 작성자 : 김예령
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
public class Company extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "companyNum")
    private Long companyNum;                 //번호

    @Column(name = "companyBrand")
    private String companyBrand;             //브랜드명

    @Column(name = "companyType")
    private String companyType;              //조직 타입(3 중 1)

    @Column(name = "companyName")
    private String companyName;              //이름(상호명)

    @Column(name = "companyPhone")
    private String companyPhone;             //전화번호

    @Column(name = "companyEmail")
    private String companyEmail;             //이메일

    @Column(name = "companyAddress")
    private String companyAddress;           //주소

    @Column(name = "companyCeoName")
    private String companyCeoName;           //대표자명

    @Column(name = "companyBusinessNum")
    private String companyBusinessNum;       //사업자등록번호

    private String companyPictureMeta;      //등록 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyParent")
    private Company companyParent;

}