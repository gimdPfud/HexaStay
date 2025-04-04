/***********************************************
 * 클래스명 : HotelRoomDTO
 * 기능 : HotelRoomDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 BaseEntity 추가, 기존 날짜 필드 삭제, 상태 체크인 체크아웃 필드 추가 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Collection;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hotelRoom")

public class HotelRoom extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotelRoomNum")
    private Long hotelRoomNum;                  //번호

    @Column(name = "hotelRoomName")
    private String hotelRoomName;               //방 이름

    @Column(name = "hotelRoomPhone")
    private String hotelRoomPhone;              //방 전화번호

    @Column(name = "hotelRoomType")
    private String hotelRoomType;               //종류 (싱글 더블 스위트 ..)

    @Column(name = "hotelRoomContent")
    private String hotelRoomContent;            //방 상세설명

    @Column(name = "hotelRoomStatus")
    private boolean hotelRoomStatus;            //활성화 상태

    @Column(name = "hotelRoomPrice")
    private Integer hotelRoomPrice;              //가격

               //체크인    - BaseEntity 로 대체
             //체크아웃 -BaseEntity로 대체



    //****룸의 비밀번호 설정 컬럼모음**********//
    @Column(name = "hotelRoomQr")
    private String hotelRoomQr;                 //Qr명

    @Column(name = "hotelRoomPassword")
    private String hotelRoomPassword;           //비밀번호
    //****룸의 권한 설정 컬럼모음**********//


    //*********참조 테이블 모음*********//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchNum")
    private Branch branch;             //지사 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facilityNum")
    private Facility facility;         //시설 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberNum")
    private Member member;
    //*********참조 테이블 모음*********//

}