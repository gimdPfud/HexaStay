/***********************************************
 * 클래스명 : OrderRoom
 * 기능 : OrderRoom 엔티티
 * 작성자 : 깁부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 BaseEntity 추가, 기존 날짜 필드 삭제, Member 참조 추가 : 김예령
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
public class MaechulRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maechulRoomNum")
    private Long maechulRoomNum;



    //호텔 숙박 가격 총 매출액
    private Long maechulRoomTotalPrice;



    //Room테이블 - HotelRoom
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom hotelRoom;
    //todo:1. Room정보  hotelRoomName 방이름 가져오기
    //todo:2. Room정보 hotelRoomPrice 룸의 가격 가져오기


    //Room테이블 - HotelRoom
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberNum")
    private Member member;





}