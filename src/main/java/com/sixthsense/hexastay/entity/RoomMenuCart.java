package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/***************************************************
 *
 * 클래스명 : RoomMenuCart
 * 기능 : 룸서비스에서 장바구니를 위한 Entity
 *        사용자가 장바구니를 생성하고 이를 통해 여러 CartItem들을 관리한다.
 *        각 장바구니는 여러 항목(RoomMenuCartItem)을 포함하며,
 *        사용자는 하나의 장바구니를 가질 수 있다.
 *        장바구니에는 해당 사용자의 장바구니 항목들 및 총 가격이 포함된다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-02
 * 수정일 : -
 * 테이블설계 : 김윤겸
 *
 ****************************************************/

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "roomMenuCart")
public class RoomMenuCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuCartNum; // 장바구니의 pk

    private Integer totalPrice; // 장바구니의 총 가격

    @OneToOne
    @JoinColumn(name = "memberNum")
    private Member member;  // 사용자와 장바구니의 연관관계

    @OneToMany(mappedBy = "roomMenuCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomMenuCartItem> roomMenuCartItems; // 장바구니 항목들

    // 장바구니의 총 가격을 계산하는 메서드
    public void calculateTotalPrice() {
        this.totalPrice = roomMenuCartItems.stream()
                .mapToInt(RoomMenuCartItem::calculatePrice)  // 각 항목의 가격을 계산하여 합산
                .sum();
    }


}
