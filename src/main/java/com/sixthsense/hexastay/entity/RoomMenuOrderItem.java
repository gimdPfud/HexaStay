package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"orders"})
@NoArgsConstructor
public class RoomMenuOrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomMenuOrderItemNum")
    private Long roomMenuOrderItemNum;

    @ManyToOne(fetch = FetchType.LAZY)  //다대일
    @JoinColumn(name = "roomMenu")
    private RoomMenu roomMenu;

    @ManyToOne(fetch = FetchType.LAZY)  //다대일
    @JoinColumn(name = "order_id")
    private RoomMenuOrder roomMenuOrder;

    private Integer roomMenuOrderPrice;  //주문가격

    @Column(length = 1000)
    private String roomMenuOrderRequestMessage; // 주문요청사항

    private int roomMenuOrderAmount; //주문수량

    public int calculateTotalPrice(RoomMenuOrder order) {
        return order.getOrderItems().stream()
                .mapToInt(item -> item.getRoomMenuOrderPrice() * item.getRoomMenuOrderAmount())
                .sum();
    }
}
