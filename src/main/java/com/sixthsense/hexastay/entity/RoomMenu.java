/**************************************************
 * 클래스명 : RoomMenu
 * 기능   : 룸서비스 메뉴 정보를 나타내는 엔티티 클래스입니다.
 * 각 메뉴의 이름, 가격, 수량, 설명, 카테고리, 상태, 이미지 정보 등을 관리하며,
 * 객실(Room) 정보와 다국어 지원 및 개발자 승인 상태도 포함합니다.
 * Lombok 어노테이션을 사용하여 getter, setter, builder 등을 간편하게 생성합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-03-31
 * 수정일 : 2025-05-09
 * 주요 필드 : roomMenuNum (PK), roomMenuName, roomMenuPrice, roomMenuAmount, roomMenuContent, roomMenuCategory, roomMenuStatus, roomMenuImageMeta, room (FK), supportsMultilang, approvedByDev
 **************************************************/

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

    @Column(name = "roomMenuLikes")
    private Integer roomMenuLikes = 0; // 좋아요

    @Column(name = "roomMenuDisLikes")
    private Integer roomMenuDisLikes = 0; // 싫어요

    @Column(name = "roomMenuImageMeta")
    private String roomMenuImageMeta; //룸 메뉴 대표 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room")
    private Room room;   // 룸(방)의 정보 참조

    @Column(name = "supportsMultilang")
    private Boolean supportsMultilang = false;  // 다국어 체크 여부

    @Column(name = "approvedByDev")
    private Boolean approvedByDev = false; // 개발자 승인 여부


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