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





}
