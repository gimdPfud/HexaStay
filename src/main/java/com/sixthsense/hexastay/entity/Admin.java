/***********************************************
 * 클래스명 : Admin
 * 기능 : Admin 엔티티
 * 작성자 :
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
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adminNum")
    private Long adminNum;

    //어드민 패스워드
    private Long adminPassword;

    //어드민 이름
    private String adminName;

    //어드민 이메일
    private String adminEmail;

    //어드민 전화번호
    private Long adminPhone;

    //어드민 직책
    private String adminPosition;

    //어드미Brand
    private String adminBrand;

    //어드민 생성일
    private LocalDateTime adminCreateDate;

    //어드민 수정일
    private LocalDateTime adminModifyDate;



}