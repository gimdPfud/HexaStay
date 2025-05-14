package com.sixthsense.hexastay.entity;


import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_num")
    private Long roomNum;

    @Column(name = "room_name")
    private String roomName;

    @Column(nullable = false)
    private String roomDisplayStatus;  // VISIBLE, HIDDEN 등

    private String roomPassword;    //Room이용 password

    //체크인 체크 // 체크 아웃
    private LocalDateTime checkInDate;

    private LocalDateTime checkOutDate;



    //*****참조 테이블****//
    //1.Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member member;

    //2.HotelRoom
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom hotelRoom;

}
