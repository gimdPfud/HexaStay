package com.sixthsense.hexastay.entity;


import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter @Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomNum;


    //*****참조 테이블****//
    //1.Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberNum")
    private Member member;

    //2.HotelRoom
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom hotelRoom;

}
