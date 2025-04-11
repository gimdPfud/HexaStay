/***********************************************
 * 클래스명 : RoomMenu
 * 기능 : RoomMenu 엔티티
 * 작성자 : 김윤겸
 * 작성일 : 2025-03-31
 * 수정 :
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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


    @Column(name = "roomMenuImageMeta")
    private String roomMenuImageMeta; //룸 메뉴 대표 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotelRoomNum")
    private HotelRoom hotelRoom;                    //방 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member")
    private Member member;   // 회원 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room")
    private Room room;   // 룸(방)의 정보 참조

    public void roomMenuOrderStockNumber(Integer stockNumber) {
        // 수량을 받아 주문이나 주문취소수량을 받아서 재고를 확인 후 재고 수량을 변경

        if (this.roomMenuAmount - stockNumber < 0){
            throw new IllegalArgumentException("요청 수량이 현재 재고보다 많습니다. (현재수량 : " + this.roomMenuAmount +")");
        }

        this.roomMenuAmount = this.roomMenuAmount - stockNumber;

    }



}