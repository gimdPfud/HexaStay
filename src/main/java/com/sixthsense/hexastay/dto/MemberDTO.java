/***********************************************
 * 클래스명 : MemberDTO
 * 기능 : MemberDTO 엔티티
 * 작성자 : 김예령
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
public class MemberDTO {
    private Long memberNum;                             //번호

    private String memberName;                          //이름

    private String memberPassword;                      //비밀번호

    private String memberPhone;                         //전화번호

    private String memberEmail;                         //이메일




    private LocalDateTime createDate;       //가입 일자

    //체크인 - 체크아웃 데이터 가져 오기
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;


    private String memberRole;

    private String hotelRoomType;   //0411

    
    // FK용
    private Long centerNum;

    private Long branchNum;

    private Long facilityNum;

    private Long hotelRoomNum;


    //*******************//
    private HotelRoomDTO hotelRoomDTO;

    public MemberDTO(Member member) {
        this.memberNum = member.getMemberNum();
        this.memberName = member.getMemberName();
        this.memberPhone = member.getMemberPhone();
        this.memberEmail = member.getMemberEmail();



    }

    public MemberDTO setHotelRoomDTO(HotelRoomDTO hotelRoomDTO) {

        this.hotelRoomDTO = hotelRoomDTO;

        return this;
    }


}
