/***********************************************
 * 클래스명 : FsViewDTO
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-05-01
 * 수정 : 2025-05-01
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FsViewDTO {
    private Long facilNum;
    private String facilName;
    private String facilAddress;
    private List<FsssViewDTO> fssList = new ArrayList<>();

    public FsViewDTO(CompanyDTO cdto) {
        this.facilNum = cdto.getCompanyNum();
        this.facilName = cdto.getCompanyName();
        this.facilAddress = cdto.getCompanyAddress();
    }
}
