package com.sixthsense.hexastay.entity;
import jakarta.persistence.*;
import lombok.*;

/**************************************************
 * 클래스명 : RoomMenuCartItemOption
 * 기능   : 룸서비스 장바구니 항목에 선택된 개별 옵션을 나타내는 엔티티 클래스입니다.
 * 각 레코드는 특정 장바구니 항목(RoomMenuCartItem)에 대해 선택된
 * 원본 옵션(RoomMenuOption)의 정보(이름, 가격, 수량)를 저장합니다.
 * Lombok 어노테이션을 사용하여 getter, setter, builder 등을 간편하게 생성합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-27
 * 수정일 : 2025-05-09
 * 주요 필드 : roomMenuCartItemOptionNum (PK), roomMenuCartItemOptionName,
 * roomMenuCartItemOptionPrice, roomMenuCartItemOptionAmount, roomMenuCartItem (FK),
 * roomMenuOption (FK)
 **************************************************/

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RoomMenuCartItemOption")

public class RoomMenuCartItemOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuCartItemOptionNum;

    private String roomMenuCartItemOptionName;
    private Integer roomMenuCartItemOptionPrice;
    private Integer roomMenuCartItemOptionAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomMenuCartItem")
    private RoomMenuCartItem roomMenuCartItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomMenuOption") // 실제 RoomMenuOption 엔티티의 PK를 참조하는 외래키 컬럼
    private RoomMenuOption roomMenuOption; // 옵션 마스터 데이터 참조


}
