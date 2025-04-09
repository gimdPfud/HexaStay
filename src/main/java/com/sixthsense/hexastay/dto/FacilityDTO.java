/***********************************************
 * 클래스명 : FacilityDTO
 * 기능 : FacilityDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityDTO {
    private Long facilityNum;

    private String facilityName;                //이름

    private String facilityPhone;               //전화번호

    private String facilityEmail;               //이메일

    private String facilityAddress;             //주소

    private String facilityCeoName;             //대표 이름

    private String facilityBusinessNum;       //사업자등록번호

    private LocalDateTime createDate; //등록일자

    private LocalDateTime modifyDate; //수정일자

    private Long centerNum;              //본사 참조

    private String centerName;

    private String centerBrand;

    private String companyPictureMeta;

    private MultipartFile companyPicture;


}