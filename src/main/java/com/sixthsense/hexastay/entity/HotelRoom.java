/***********************************************
 * 클래스명 : HotelRoomDTO
 * 기능 : HotelRoomDTO 엔티티
 * 작성자 :
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hotelRoom")
public class HotelRoom {
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

    @Column(name = "hotelRoomQr")
    private String hotelRoomQr;                 //Qr명

    @CreatedDate
    @Column(name = "hotelRoomCreateDate")
    private LocalDateTime hotelRoomCreateDate;  //등록일자

    @LastModifiedDate
    @Column(name = "hotelRoomModifyDate")
    private LocalDateTime hotelRoomModifyDate;  //수정일자

    @Column(name = "hotelRoomContent")
    private String hotelRoomContent;            //방 상세설명

    @Column(name = "hotelRoomPrice")
    private Integer hotelRoomPrice;              //가격

    @Column(name = "hotelRoomPassword")
    private String hotelRoomPassword;           //비밀번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centerNum")
    private Center hotelRoomCenter;             //센터 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchNum")
    private Branch hotelRoomBranch;             //지사 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facilityNum")
    private Facility hotelRoomFacility;         //시설 참조
}