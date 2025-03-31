/***********************************************
* 클래스명 : ReviewDTO
* 기능 : ReviewDTO 엔티티
* 작성자 : 
* 작성일 : 2025-03-31
* 수정 : 2025-03-31
* ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewNum")
    private Long reviewNum;                 //번호

    @Column(name = "reviewTitle")
    private String reviewTitle;             //제목

    @Column(name = "reviewContent")
    private String reviewContent;           //내용

    @Column(name = "reviewRating")
    private Integer reviewRating;           //별점

    @Column(name = "reviewView")
    private Integer reviewView;             //조회수

    @Column(name = "reviewCreateDate")
    private LocalDateTime reviewCreateDate; //등록일자

    @Column(name = "reviewModifyDate")
    private LocalDateTime reviewModifyDate; //수정일자

    @Column(name = "reviewPassword")
    private String reviewPassword;          //등록비밀번호

    @ManyToOne
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom reviewHotelRoom;      //방 참조

    @ManyToOne
    @JoinColumn(name = "memberNum")
    private Member reviewMember;            //회원 참조
}