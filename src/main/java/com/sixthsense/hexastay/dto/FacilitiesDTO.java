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

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilitiesDTO {

    private Long facilitiesNum;
    private String fsName;      //서비스 이름
    private String fsContent;   //서비스 설명
    private Integer fsPrice;    //서비스 가격
    private Integer fsAmountMax;   //서비스 수량(수용인원으로 응용??)
    private Integer fsAmount;   //서비스 수량(수용인원으로 응용??)
    private String fsStatus;    //서비스 상태(이용가능 불가능 삭제됨)
    private CompanyDTO companyDTO;

    private String fsPictureMeta;       //등록 이미지
    private MultipartFile fsPicture;    //이미지 경로

    public FacilitiesDTO setCompanyDTO(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
        return this;
    }
}