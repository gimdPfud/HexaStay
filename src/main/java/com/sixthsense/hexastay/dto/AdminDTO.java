package com.sixthsense.hexastay.dto;

/********************************************
 * 클래스명 : AdminDTO
 * 기능 : AdminDTO
 * 작성자 : 김부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDTO {


    private Long adminNum;

    //어드민 사번
    private Integer adminEmployeeNum;

    //어드민 패스워드
    private Long adminPassword;

    //어드민 이름
    private String adminName;

    //어드민 주민번호
    private Integer adminResidentNum;;

    //어드민 이메일
    private String adminEmail;

    //어드민 주소
    private String adminAddress;

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


    //어드민 승인처리
    private Boolean adminActive;

}
