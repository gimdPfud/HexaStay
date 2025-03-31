/***********************************************
 * 클래스명 : OrderRoom
 * 기능 : OrderRoom 엔티티
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
public class OrderRoom {
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

    //호텔숙박매출일
    private LocalDateTime orderRoomCreateDate;

    //호텔숙박매출일자
    private LocalDateTime orderRoomModifyDate;

    //***참조 테이블************************
    //Room테이블 - HotelRoom
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom hotelRoom;


    //멤버 테이블





}