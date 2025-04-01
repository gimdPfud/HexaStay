/***********************************************
 * 클래스명 : CenterDTO
 * 기능 : CenterDTO 엔티티
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
public class Center extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "centerNum")
    private Long centerNum;                 //번호

    @Column(name = "centerBrand")
    private String centerBrand;             //브랜드명

    @Column(name = "centerName")
    private String centerName;              //이름(상호명)

    @Column(name = "centerPhone")
    private String centerPhone;             //전화번호

    @Column(name = "centerEmail")
    private String centerEmail;             //이메일

    @Column(name = "centerAddress")
    private String centerAddress;           //주소

    @Column(name = "centerCeoName")
    private String centerCeoName;           //대표자명

    @Column(name = "centerBusinessNum")
    private String centerBusinessNum;       //사업자등록번호
}