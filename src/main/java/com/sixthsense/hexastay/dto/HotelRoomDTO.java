/***********************************************
 * 클래스명 : HotelRoomDTO
 * 기능 : HotelRoomDTO 엔티티
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 날짜 필드 이름 수정 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.Member;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
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

    private String hotelRoomContent;            //방 상세설명

    private boolean hotelRoomStatus;            //활성화 상태

    private Integer hotelRoomPrice;              //가격


    //*********숙박 일정 필드명********//
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime checkInDate; //체크인

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime checkOutDate; //체크아웃


    //****룸의 비밀번호 설정 컬럼모음**********//
    private String hotelRoomQr;                 //Qr명

    private String hotelRoomPassword;           //비밀번호
    //****룸의 권한 설정 컬럼모음**********//


    //todo:참조 참조 구분 하기 편하게
    //*********참조 PK PK PK PK 모음************//
    private Long branchNum;             //지사 참조

    private Long facilityNum;         //시설 참조

    private Long memberNum;  //Member 참조    -pk
    //*********참조 PK PK PK PK 모음************//


    //todo : 추후에 Member 에 특정 값 만 가져 오기 위힌 것임
     //*****Member 테이블 참조******************//참조 클래스
    //필드 에서 MemberDTO가 처음 부터 HotelDTO가 가지고 가는 방식의 메소드 로직
    private MemberDTO memberDTO;

    //ModelMapper를 이용해서 한번에 참조 값을 변환 하기 위한 메서드
    public HotelRoomDTO setMember(MemberDTO memberDTO) {

        this.memberDTO = memberDTO;

        return this;
    }








}