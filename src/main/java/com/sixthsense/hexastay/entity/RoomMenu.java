/***********************************************
 * 클래스명 : RoomServiceDTO
 * 기능 : RoomServiceDTO 엔티티
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
@Table(name = "roomMenu")
public class RoomMenu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomMenuNum")
    private Long roomMenuNum;

    @Column(name = "roomMenuName")
    private String roomMenuName;                 //상품이름

    @Column(name = "roomMenuPrice")
    private Integer roomMenuPrice;                //상품가격

    @Column(name = "roomMenuAmount")
    private Integer roomMenuAmount;                 // 상품수량

    @Column(name = "roomMenuContent")
    private String roomMenuContent;              //상품설명

    @Column(name = "roomMenuCategory")
    private String roomMenuCategory;             //카테고리

    @Column(name = "roomMenuStatus")
    private String roomMenuStatus;              //품절여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom hotelRoom;                    //방 참조
}