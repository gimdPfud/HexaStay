package com.sixthsense.hexastay.dto;


import lombok.*;

@Getter@Setter@ToString
@AllArgsConstructor @NoArgsConstructor
@Builder

public class RoomDTO {

        private Long roomNum;


        //*****참조 PK 테이블****//
        //1.Member
        private Long memberNum;

        //2.HotelRoom
        private Long hotelRoomNum;

        // 새로운 필드 추가 (호텔 룸 정보)
        private String hotelRoomName;
        private String hotelRoomPhone;

        // 새로운 필드 추가 (멤버 정보)
        private String memberName;
        private String memberEmail;


        private MemberDTO memberDTO;

        private HotelRoomDTO hotelRoomDTO;


}
