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
public class FacViewDTO {
    private Long facNum;
    private String facName;
    private String facAddress;
    private String facLatitude;             //좌표1 위도?
    private String facLongitude;            //좌표2 경도?
    private String facWtmX;                 //좌표3 자세한 x좌표
    private String facWtmY;                 //좌표4 자세한 y좌표
    private String facPictureMeta;
    private List<FsssViewDTO> fssList = new ArrayList<>();

    public FacViewDTO(CompanyDTO cdto) {
        this.facNum = cdto.getCompanyNum();
        this.facName = cdto.getCompanyName();
        this.facAddress = cdto.getCompanyAddress();
        this.facLatitude = cdto.getCompanyLatitude();             //좌표1 위도?
        this.facLongitude = cdto.getCompanyLongitude();           //좌표2 경도?
        this.facWtmX = cdto.getCompanyWtmX();                     //좌표3 자세한 x좌표
        this.facWtmY = cdto.getCompanyWtmY();                     //좌표4 자세한 y좌표
        this.facPictureMeta = cdto.getCompanyPictureMeta();
    }
    public void addFssList(FacilitiesDTO dto){
        fssList.add(new FsssViewDTO(dto));
    }
}