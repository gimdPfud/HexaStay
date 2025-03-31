/***********************************************
 * 클래스명 : BranchDTO
 * 기능 : BranchDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchDTO {
    private Long branchNum;                 //번호

    private String branchName;              //이름

    private String branchPhone;             //전화번호

    private String branchEmail;             //이메일

    private String branchAddress;           //주소

    private String branchCeoName;           //대표명

    private LocalDateTime branchCreateDate; //등록일자

    private CenterDTO branchCenterDTO;            //센터 참조
}