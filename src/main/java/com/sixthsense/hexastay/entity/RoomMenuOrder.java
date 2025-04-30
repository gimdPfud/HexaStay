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
    @JoinColumn(name = "hotelRoom") // DB의 외래 키 컬럼 이름
    private HotelRoom hotelRoom;

    // 연관관계 편의 메소드 (양방향 시 필요할 수 있음)
    public void addOrderItem(RoomMenuOrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setRoomMenuOrder(this);
    }


}
