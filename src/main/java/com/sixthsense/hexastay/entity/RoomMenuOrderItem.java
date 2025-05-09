package com.sixthsense.hexastay.entity;
import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**************************************************
 * 클래스명 : RoomMenuOrderItem
 * 기능   : 룸서비스 주문에 포함된 개별 항목(메뉴)을 나타내는 엔티티 클래스입니다.
 * 각 주문 항목은 특정 룸서비스 메뉴, 원본 주문, 주문 당시의 가격, 요청 메시지, 주문 수량 및
 * 선택된 옵션 정보를 가집니다.
 * Lombok 어노테이션을 사용하여 getter, setter 등을 간편하게 생성하며, BaseEntity를 상속합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-11
 * 수정일 :
 * 주요 필드 : roomMenuOrderItemNum (PK), roomMenu (FK), roomMenuOrder (FK),
 * roomMenuOrderPrice, roomMenuOrderAmount, roomMenuSelectOptionName,
 * roomMenuSelectOptionPrice
 **************************************************/

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
    @JoinColumn(name = "roomMenuOrderNum")
    private RoomMenuOrder roomMenuOrder;

    private Integer roomMenuOrderPrice;  //주문가격

    @Column(length = 1000)
    private String roomMenuOrderRequestMessage; // 주문요청사항

    private int roomMenuOrderAmount; //주문수량

    private String roomMenuSelectOptionName;  // 옵션의 이름.

    private Integer roomMenuSelectOptionPrice; //옵션의 가격

}
