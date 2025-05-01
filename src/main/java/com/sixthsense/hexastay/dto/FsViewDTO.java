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
    private String facilLatitude;             //좌표1 위도?
    private String facilLongitude;            //좌표2 경도?
    private String facilWtmX;                 //좌표3 자세한 x좌표
    private String facilWtmY;                 //좌표4 자세한 y좌표
    private String facilPictureMeta;
    private List<FsssViewDTO> fssList = new ArrayList<>();

    public FsViewDTO(CompanyDTO cdto) {
        this.facilNum = cdto.getCompanyNum();
        this.facilName = cdto.getCompanyName();
        this.facilAddress = cdto.getCompanyAddress();
        this.facilLatitude = cdto.getCompanyLatitude();             //좌표1 위도?
        this.facilLongitude = cdto.getCompanyLongitude();           //좌표2 경도?
        this.facilWtmX = cdto.getCompanyWtmX();                     //좌표3 자세한 x좌표
        this.facilWtmY = cdto.getCompanyWtmY();                     //좌표4 자세한 y좌표
        this.facilPictureMeta = cdto.getCompanyPictureMeta();
    }
    public void addFssList(FacilitiesDTO dto){
        fssList.add(new FsssViewDTO(dto));
    }
}