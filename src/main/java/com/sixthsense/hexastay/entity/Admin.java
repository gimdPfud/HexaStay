/***********************************************
 * 클래스명 : Admin
 * 기능 : Admin 엔티티
 * 작성자 : 깁부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 BaseEntity 추가, 기존 날짜 필드 삭제 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adminNum")
    private Long adminNum;

    //어드민 사번
    private Integer adminEmployeeNum;

    //어드민 패스워드
    private String adminPassword;

    //어드민 이름
    private String adminName;

    //어드민 이메일
    private String adminEmail;

    //어드민 주소
    private String adminAddress;

    //어드민 주민번호
    private String adminResidentNum;

    //어드민 전화번호
    private String adminPhone;

    //어드민 직급
    private String adminRole;

    //어드민 Brand
    private String adminBrand;

    //어드민 승인여부
    private Boolean adminActive;

    //어드민 직책
    private String adminPosition;

    //어드민 입사일
    private LocalDate adminJoinDate;

}