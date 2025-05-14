/***********************************************
 * 클래스명 : FacilityDTO
 * 기능 : FacilityDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilitiesDTO {

    private Long facilitiesNum;
    private String facTitle;      //서비스 이름
    private String facContent;   //서비스 설명
    private CompanyDTO companyDTO;

    public FacilitiesDTO setCompanyDTO(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
        return this;
    }
}