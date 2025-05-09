package com.sixthsense.hexastay.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**************************************************
 * 클래스명 : RoomMenuLike
 * 기능   : 룸서비스 메뉴에 대한 사용자의 '좋아요/싫어요' 상태를 나타내는 엔티티 클래스입니다.
 * 각 레코드는 특정 회원(Member)이 특정 룸서비스 메뉴(RoomMenu)에 대해 표시한 선호도를 저장합니다.
 * Lombok 어노테이션을 사용하여 getter, setter 등을 간편하게 생성합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-14
 * 수정일 :
 * 주요 필드 : roomMenuLikeNum (PK), roomMenuLikedCheck, member (FK), roomMenu (FK)
 **************************************************/

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor

public class RoomMenuLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuLikeNum; // 좋아요의 pk

    private Boolean roomMenuLikedCheck; // true = 좋아요, false = 싫어요

    @ManyToOne
    private Member member;

    @ManyToOne
    private RoomMenu roomMenu;

}
