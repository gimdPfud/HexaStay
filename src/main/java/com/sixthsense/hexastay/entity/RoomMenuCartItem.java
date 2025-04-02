package com.sixthsense.hexastay.entity;
import jakarta.persistence.*;
import lombok.*;

/***************************************************

 * 클래스명 : RoomMenuCartItem
 * 기능 : 장바구니 항목을 나타내는 Entity 클래스
 *        하나의 장바구니 항목은 특정 상품(RoomMenu)과 수량 및 가격을 나타낸다.
 *        장바구니에 담긴 각 상품의 정보를 관리하며,
 *        해당 상품이 장바구니에 속하는 관계를 맺고 있다.
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roomMenuCartItem")
public class RoomMenuCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuCartItemNum;  // 장바구니 항목의 고유 ID

    private Integer roomMenuCartItemAmount;  // 상품 수량
    private Integer roomMenuCartItemPrice;     // 해당 상품의 가격 (수량 * 가격)

    @ManyToOne
    @JoinColumn(name = "roomMenuNum")  // 어떤 상품(메뉴)인지
    private RoomMenu roomMenu;         // 해당 상품(메뉴)

    @ManyToOne
    @JoinColumn(name = "roomMenuCartNum")  // 어떤 장바구니에 속하는지
    private RoomMenuCart roomMenuCart;     // 장바구니와의 연관 관계

    // 가격을 계산하는 메서드
    public Integer calculatePrice() {
        return roomMenu.getRoomMenuPrice() * this.roomMenuCartItemAmount;  // 가격 = 상품 가격 * 수량
    }

}
