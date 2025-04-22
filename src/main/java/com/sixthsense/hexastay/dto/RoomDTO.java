package com.sixthsense.hexastay.dto;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter@Setter@ToString
@AllArgsConstructor @NoArgsConstructor
@Builder

public class RoomDTO {

        private Long roomNum;

        private String roomPassword;    //Room이용 password


        /**Room 테이블에 체크인 - 체크아웃 데이터 추가**/

        private LocalDateTime checkInDate;


        private LocalDateTime checkOutDate;



        //*****참조 PK 테이블****//
        //1.Member
        private Long memberNum;

        //2.HotelRoom
        private Long hotelRoomNum;


        //3.Room DB 생성 일자
        private LocalDateTime createDate;



        // 새로운 필드 추가 (호텔 룸 정보)
        private String hotelRoomName;
        private String hotelRoomPhone;

        // 새로운 필드 추가 (멤버 정보)
        private String memberName;
        private String memberEmail;

        private LocalDateTime membereDate;      //Member 방배정 등록 기준 날짜


        private MemberDTO memberDTO;

        private HotelRoomDTO hotelRoomDTO;


}
