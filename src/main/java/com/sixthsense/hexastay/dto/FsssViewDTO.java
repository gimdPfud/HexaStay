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
    private String fsssName;      //서비스 이름
    private String fsssContent;   //서비스 설명
    private Integer fsssPrice;    //서비스 가격
    private Integer fsssAmountMax;   //서비스 수량(수용인원으로 응용??)
    private Integer fsssAmount;   //서비스 수량(수용인원으로 응용??)
    private String fsssStatus;    //서비스 상태(이용가능 불가능 삭제됨)
    private Long fsNum;

    public FsssViewDTO(FacilitiesDTO dto) {
        this.fsssNum = dto.getFacilitiesNum();
        this.fsssName = dto.getFsName();
        this.fsssContent = dto.getFsContent();
        this.fsssPrice = dto.getFsPrice();
        this.fsssAmountMax = dto.getFsAmountMax();
        this.fsssAmount = dto.getFsAmount();
        this.fsssStatus = dto.getFsStatus();
        this.fsNum = dto.getCompanyDTO().getCompanyNum();
    }
}