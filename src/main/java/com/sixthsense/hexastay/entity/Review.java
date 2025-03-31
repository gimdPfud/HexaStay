/***********************************************
* 클래스명 : ReviewDTO
* 기능 : ReviewDTO 엔티티
* 작성자 : 김예령
* 작성일 : 2025-03-31
* 수정 : 2025-03-31 BaseEntity 추가, 기존 날짜 필드 삭제 : 김예령
* ***********************************************/
package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {
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

    @Column(name = "reviewPassword")
    private String reviewPassword;          //등록비밀번호

    @ManyToOne
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom hotelRoom;            //방 참조

    @ManyToOne
    @JoinColumn(name = "memberNum")
    private Member member;                  //회원 참조
}