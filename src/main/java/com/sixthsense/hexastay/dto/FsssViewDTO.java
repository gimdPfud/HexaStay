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
public class FsssViewDTO {

    private Long fsssNum;
    private String fsssTitle;      //서비스 이름
    private String fsssContent;   //서비스 설명
    private Long fcNum;

    public FsssViewDTO(FacilitiesDTO fdto) {
        this.fsssNum = fdto.getFacilitiesNum();
        this.fsssTitle = fdto.getFacTitle();
        this.fsssContent = fdto.getFacContent();
        this.fcNum = fdto.getCompanyDTO().getCompanyNum();
    }
}