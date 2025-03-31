/***********************************************
 * 클래스명 : HotelRoomDTO
 * 기능 : HotelRoomDTO 엔티티
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelRoomDTO {
    private Long hotelRoomNum;                  //번호

    private String hotelRoomName;               //방 이름

    private String hotelRoomPhone;              //방 전화번호

    private String hotelRoomType;               //종류 (싱글 더블 스위트 ..)

    private String hotelRoomQr;                 //Qr명

    private LocalDateTime hotelRoomCreateDate;  //등록일자

    private LocalDateTime hotelRoomModifyDate;  //수정일자

    private String hotelRoomContent;            //방 상세설명

    private Integer hotelRoomPrice;              //가격

    private String hotelRoomPassword;           //비밀번호

    private CenterDTO hotelRoomCenterDTO;             //센터 참조

    private BranchDTO hotelRoomBranchDTO;             //지사 참조

    private FacilityDTO hotelRoomFacilityDTO;         //시설 참조
}