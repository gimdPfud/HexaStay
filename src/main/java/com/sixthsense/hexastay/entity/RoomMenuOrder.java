package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import com.sixthsense.hexastay.enums.RoomMenuOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**************************************************
 * 클래스명 : RoomMenuOrder
 * 기능   : 룸서비스 주문 정보를 나타내는 엔티티 클래스입니다.
 * 주문자(Member), 주문 상태, 주문일, 할인 정보, 총액 및 주문 항목(RoomMenuOrderItem) 목록을 포함합니다.
 * 하나의 주문은 특정 객실(Room) 및 호텔 객실(HotelRoom)과 연관될 수 있습니다.
 * Lombok 어노테이션을 사용하여 getter, setter 등을 간편하게 생성하며, BaseEntity를 상속합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-11
 * 수정일 :
 * 주요 필드 : roomMenuOrderNum (PK), member (FK), roomMenuOrderStatus, regDate,
 * discountedPrice, originalTotalPrice, usedCouponNum, room (FK), hotelRoom (FK)
 **************************************************/

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RoomMenuOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomMenuOrderNum")
    private Long roomMenuOrderNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member")  // 외래키 컬럼도 명확하게
    private Member member;

    @Enumerated(EnumType.STRING)
    private RoomMenuOrderStatus roomMenuOrderStatus;

    @Column(name = "regDate")
    private LocalDateTime regDate;

    private Integer discountedPrice; // 할인된 최종 금액

    private Integer originalTotalPrice; // 쿠폰 사용 전 가격

    private Long usedCouponNum; // 사용된 쿠폰의 넘버

    @OneToMany(mappedBy = "roomMenuOrder", cascade = CascadeType.ALL)
    private List<RoomMenuOrderItem> orderItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room") // 이 주문이 발생한 HotelRoom의 외래키
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY) // FetchType은 상황에 맞게
    @JoinColumn(name = "hotel_room_num") // DB의 외래 키 컬럼 이름
    private HotelRoom hotelRoom;

}
