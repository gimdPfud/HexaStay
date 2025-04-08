package com.sixthsense.hexastay.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaechulRoomDTO {

    private Long orderRoomNum;


    //호텔 수박가격 총 매출액
    private Long orderRoomTotalPrice;



    //매출 발생 일자
    private LocalDateTime createDate;


    //***참조 테이블 PK PK PK PK **************//
    //Room테이블 - HotelRoom
    private Long hotelRoomNum;

    private Long memberNum;
    //***참조 테이블 PK PK PK PK **************//

    //todo : 참조 DTO들을 orderRoomDTO에 담기
    //******HotelRoom********//
    private HotelRoomDTO hotelRoomDTO;
    public MaechulRoomDTO setHotelRoomDTO(HotelRoomDTO hotelRoom) {

        this.hotelRoomDTO = hotelRoomDTO;

        return this;
    }

    //******Member********//
    private MemberDTO memberDTO;

    public MaechulRoomDTO setMemberDTO(MemberDTO memberDTO) {

        this.memberDTO = memberDTO;

        return this;
    }

}
