package com.sixthsense.hexastay.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

/**************************************************
 * 클래스명 : RoomMenuCartItem
 * 기능   : 룸서비스 장바구니에 담긴 개별 항목(상품)을 나타내는 엔티티 클래스입니다.
 * 각 항목은 특정 룸서비스 메뉴, 수량, 선택된 옵션 정보 및 가격을 포함하며,
 * 특정 장바구니(RoomMenuCart)와 객실(Room)에 속합니다.
 * Lombok 어노테이션을 사용하여 getter, setter, builder 등을 간편하게 생성합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-03
 * 수정일 :
 * 주요 필드 : roomMenuCartItemNum (PK), roomMenuCartItemAmount, roomMenuCartItemCount,
 * roomMenuSelectOptionName, roomMenuSelectOptionPrice, roomMenuCartItemPrice, roomMenuCart (FK),
 * roomMenu (FK), room (FK)
 **************************************************/

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roomMenuCartItem")
public class RoomMenuCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuCartItemNum;  // 장바구니 항목의 고유 ID

    private Integer roomMenuCartItemAmount;

    private Integer roomMenuCartItemCount; // 카트에 담긴 메뉴 아이템의 갯수

    private String roomMenuSelectOptionName;  // 옵션의 이름.

    private Integer roomMenuSelectOptionPrice; //옵션의 가격

    private Integer roomMenuCartItemPrice; // 상품의 기본 가격

    @ManyToOne(fetch = FetchType.LAZY)  //다대일
    @JoinColumn(name = "cart_id")
    private RoomMenuCart roomMenuCart; // 카트참조

    @ManyToOne(fetch = FetchType.LAZY)  //다대일
    @JoinColumn(name = "item_id")
    private RoomMenu roomMenu; // 아이템 참조

    @OneToMany(mappedBy = "roomMenuCartItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomMenuCartItemOption> roomMenuCartItemOptions;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room") // Room 참조
    private Room room;


}
