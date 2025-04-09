/***********************************************
 * 클래스명 : BranchDTO
 * 기능 : BranchDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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

    private String branchBusinessNum;       //사업자등록번호

    private LocalDateTime createDate; //등록일자

    private LocalDateTime modifyDate; //수정일자

    private Long centerNum;            //센터 참조

    private String centerName;          //본사명

    private String centerBrand;         //브랜드명

    private String companyPictureMeta;

    private MultipartFile companyPicture;


}