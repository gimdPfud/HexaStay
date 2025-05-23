/***********************************************
 * 클래스명 : companyDTO
 * 기능 : companyDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.enums.CompanyType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {
    private Long companyNum;                 //번호

    private String companyBrand;             //브랜드명

    private String companyType;              //조직 타입(3 중 1)

    private String companyName;              //이름(상호명)

    private String companyPhone;             //전화번호

    private String companyEmail;             //이메일

    private String companyAddress;           //주소
    private String companyLatitude;             //좌표1 위도?
    private String companyLongitude;            //좌표2 경도?
    private String companyWtmX;                 //좌표3 자세한 x좌표
    private String companyWtmY;                 //좌표4 자세한 y좌표

    private String companyCeoName;           //대표자명

    private LocalDateTime createDate;        //등록일자

    private LocalDateTime modifyDate;        //수정일자

    private String companyBusinessNum;       //사업자등록번호

    private String companyPictureMeta;       //등록 이미지

    private MultipartFile companyPicture;    //이미지 경로

    private Long companyParent;

    private String companyParentName;         // 본사명
    private String branchName;                // 지사명
    private String facilityName;              // 지점명

    private String companyStatus;             //활성화 비활성화 상태

    public String getCompanyTypeKorean() {
        return CompanyType.displayNameFromCode(companyType);
    }
}