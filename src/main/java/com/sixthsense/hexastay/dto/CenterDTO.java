/***********************************************
 * 클래스명 : CenterDTO
 * 기능 : CenterDTO 엔티티
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
public class CenterDTO {
    private Long centerNum;                 //번호

    private String centerBrand;             //브랜드명

    private String centerName;              //이름

    private String centerPhone;             //전화번호

    private String centerEmail;             //이메일

    private String centerAddress;           //주소

    private String centerCeoName;           //대표자명

    private LocalDateTime centerCreateDate; //등록일자
}