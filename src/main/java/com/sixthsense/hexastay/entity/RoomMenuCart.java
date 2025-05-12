package com.sixthsense.hexastay.entity;
import jakarta.persistence.*;
import lombok.*;

/**************************************************
 * 클래스명 : RoomMenuCart
 * 기능   : 룸서비스 장바구니를 나타내는 엔티티 클래스입니다.
 * 한 명의 사용자는 하나의 객실(Room)에 대해 하나의 장바구니를 가질 수 있으며,
 * 이 장바구니는 여러 장바구니 항목(RoomMenuCartItem)을 포함합니다.
 * Lombok 어노테이션을 사용하여 getter, setter, builder 등을 간편하게 생성합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-02
 * 수정일 :
 * 주요 필드 : roomMenuCartNum (PK), member (FK), room (FK)
 **************************************************/

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

    @OneToOne
    @JoinColumn(name = "member")
    private Member member;  // 사용자와 장바구니의 연관관계

    @OneToOne // 하나의 Room (투숙)에 하나의 활성화된 장바구니만 있다고 가정
    @JoinColumn(name = "room") // Room 테이블의 PK 컬럼 이름
    private Room room;

}

