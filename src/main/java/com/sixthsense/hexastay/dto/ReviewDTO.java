/***********************************************
* 클래스명 : ReviewDTO
* 기능 : ReviewDTO 엔티티
* 작성자 : 
* 작성일 : 2025-03-31
* 수정 : 2025-03-31
* ***********************************************/
package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long reviewNum;                 //번호

    private String reviewTitle;             //제목

    private String reviewContent;           //내용

    private Integer reviewRating;           //별점

    private Integer reviewView;             //조회수

    private LocalDateTime reviewCreateDate; //등록일자

    private LocalDateTime reviewModifyDate; //수정일자

    private String reviewPassword;          //등록비밀번호

    private HotelRoom reviewHotelRoom;      //방 참조

    private Member reviewMember;            //회원 참조
}