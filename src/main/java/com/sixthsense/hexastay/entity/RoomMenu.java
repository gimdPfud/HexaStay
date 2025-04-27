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

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "roomMenuLikes")
    private Integer roomMenuLikes = 0; // 좋아요

    @Column(name = "roomMenuDisLikes")
    private Integer roomMenuDisLikes = 0; // 싫어요

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

    @Column(name = "supportsMultilang")
    private Boolean supportsMultilang = false;  // 다국어 체크 여부

    @Column(name = "approvedByDev")
    private Boolean approvedByDev = false; // 개발자 승인 여부

    // 룸메뉴 옵션을 선택하기 위해 맵핑되는 조인문. 하지만, cascade all 이므로 삭제 시 주의한다 !!

    public void roomMenuOrderStockNumber(Integer stockNumber) {
        // 수량을 받아 주문이나 주문취소수량을 받아서 재고를 확인 후 재고 수량을 변경

        if (this.roomMenuAmount - stockNumber < 0){
            throw new IllegalArgumentException("요청 수량이 현재 재고보다 많습니다. (현재수량 : " + this.roomMenuAmount +")");
        }

        this.roomMenuAmount = this.roomMenuAmount - stockNumber;

    }

    // 주문한 상태의 주문취소요청이 들어왔을 경우 수량을 차감하는 매소드
    public void restoreRoomMenuStockNumber(Integer stockNumber) {
        this.roomMenuAmount += stockNumber;
    }



}