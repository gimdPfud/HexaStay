package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/***************************************************
 *
 * 클래스명 : RoomMenuCart
 * 기능 : 룸서비스에서 장바구니를 위한 엔티티 클래스
 *        사용자가 하나의 장바구니를 생성하여 여러 장바구니 항목(RoomMenuCartItem)을 관리한다.
 *        각 장바구니는 여러 항목을 포함하고 있으며, 해당 장바구니에는 사용자의 장바구니 항목들과
 *        총 가격 정보가 포함된다. 사용자는 하나의 장바구니만을 가질 수 있다.
 *
 *        예시:
 *        - 사용자가 룸서비스 장바구니를 생성하고, 여러 개의 룸메뉴 항목을 담을 수 있다.
 *        - 장바구니 항목은 RoomMenuCartItem 엔티티를 통해 관리된다.
 *
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-02
 * 수정일 : 2025-04-03
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

    @OneToOne
    @JoinColumn(name = "member")
    private Member member;  // 사용자와 장바구니의 연관관계

    @OneToOne // 하나의 Room (투숙)에 하나의 활성화된 장바구니만 있다고 가정
    @JoinColumn(name = "room") // Room 테이블의 PK 컬럼 이름
    private Room room;

}

