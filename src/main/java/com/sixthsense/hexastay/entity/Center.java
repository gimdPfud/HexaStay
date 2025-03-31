/***********************************************
 * 클래스명 : CenterDTO
 * 기능 : CenterDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Center {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "centerNum")
    private Long centerNum;                 //번호

    @Column(name = "centerBrand")
    private String centerBrand;             //브랜드명

    @Column(name = "centerName")
    private String centerName;              //이름

    @Column(name = "centerPhone")
    private String centerPhone;             //전화번호

    @Column(name = "centerEmail")
    private String centerEmail;             //이메일

    @Column(name = "centerAddress")
    private String centerAddress;           //주소

    @Column(name = "centerCeoName")
    private String centerCeoName;           //대표자명

    @Column(name = "centerCreateDate")
    private LocalDateTime centerCreateDate; //등록일자
}