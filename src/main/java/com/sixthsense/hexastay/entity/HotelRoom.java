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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @Column(name = "hotelRoomLodgment")
    private Integer hotelRoomLodgment;              //숙박일수

    @Column(name = "hotelRoomPrice")
    private Integer hotelRoomPrice;              //호텔방 가격


    //****룸의 비밀번호 설정 컬럼모음**********//
    @Column(name = "hotelRoomQr")
    private String hotelRoomQr;                 //Qr명



    @Column(name = "hotelRoomPassword")
    private String hotelRoomPassword;           //비밀번호
    //****룸의 권한 설정 컬럼모음**********//



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyNum")
    private Company company;             //호텔룸 소속 커럶 추가 : 0414


    private String hotelRoomProfileMeta;      //이미지 경로 저장용 컬럼

    //*********참조 테이블 모음*********//



    // 🔹 일대다 관계 설정 (기존 ManyToOne 제거)


    public void setMember(Member memberEntity) {
    }

    public Object getMember() {

        return null;
    }
    //*********참조 테이블 모음*********//

}