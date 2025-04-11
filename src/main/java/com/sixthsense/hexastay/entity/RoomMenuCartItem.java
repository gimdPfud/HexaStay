package com.sixthsense.hexastay.entity;
import jakarta.persistence.*;
import lombok.*;

/***************************************************

 * 클래스명 : RoomMenuCartItem
 * 기능 : 장바구니 항목을 나타내는 엔티티 클래스
 *        장바구니에 담긴 각 상품(RoomMenu)과 그 상품의 수량 및 가격을 관리하는 역할을 한다.
 *        하나의 장바구니 항목은 특정 상품(RoomMenu)과 수량, 가격 정보를 가지며,
 *        해당 상품이 특정 장바구니(RoomMenuCart)에 속하는 관계를 맺고 있다.
 *
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-03
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
    @Column(name = "roomMenuCartItemNum")
    private Long roomMenuCartItemNum;  // 장바구니 항목의 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)  //다대일
    @JoinColumn(name = "roomMenuCart")
    private RoomMenuCart roomMenuCart; // 카트를 참조

    @ManyToOne(fetch = FetchType.LAZY)  //다대일
    @JoinColumn(name = "roomMenu")
    private RoomMenu roomMenu; // 메뉴를 참조

    private Integer roomMenuCartItemAmount; // 카트 아이템의 수량

}
