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
public class OrderRoom  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderRoomNum")
    private Long orderRoomNum;

    //숙박일수
    private Long orderRoomAmount;

    //호텔숙박가격
    private Long orderRoomPrice;

    //호텔 수박가격 총 매출액
    private Long orderRoomTotalPrice;


    //***참조 테이블************************
    //Room테이블 - HotelRoom
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom hotelRoom;


    //멤버 테이블
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberNum")
    private Member member;




}