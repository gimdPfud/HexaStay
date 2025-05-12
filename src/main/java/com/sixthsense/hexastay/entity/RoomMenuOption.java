package com.sixthsense.hexastay.entity;
import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**************************************************
 * 클래스명 : RoomMenuOption
 * 기능   : 룸서비스 메뉴에 적용할 수 있는 개별 옵션을 정의하는 엔티티 클래스입니다.
 * 각 옵션은 이름, 가격, 관련 수량 정보를 가지며 특정 룸서비스 메뉴(RoomMenu)에 속합니다.
 * Lombok 어노테이션을 사용하여 getter, setter 등을 간편하게 생성하며, BaseEntity를 상속받아 생성/수정일을
 * 자동 관리합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-25
 * 수정일 : 2025-05-09
 * 주요 필드 : roomMenuOptionNum (PK), roomMenuOptionName, roomMenuOptionPrice, roomMenuOptionAmount,
 * roomMenu (FK)
 **************************************************/

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor

public class RoomMenuOption extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuOptionNum; // pk

    // 옵션 이름 (예: 치즈 추가, 곱빼기 등)
    @Column(nullable = false)
    private String roomMenuOptionName;

    // 옵션 가격 (기본 단위는 원, 음수도 허용 가능)
    @Column(nullable = false)
    private int roomMenuOptionPrice;

    private int roomMenuOptionAmount;

    // 옵션이 어떤 메뉴에 속하는지
    @ManyToOne
    @JoinColumn(name = "roomMenu")
    private RoomMenu roomMenu;

}
