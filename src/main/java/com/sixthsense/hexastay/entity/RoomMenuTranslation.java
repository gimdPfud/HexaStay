package com.sixthsense.hexastay.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**************************************************
 * 클래스명 : RoomMenuTranslation
 * 기능   : 룸서비스 메뉴의 다국어 번역 정보를 저장하는 엔티티 클래스입니다.
 * 각 레코드는 특정 룸서비스 메뉴(RoomMenu)에 대해 특정 로케일('ko', 'en' 등)의
 * 메뉴 이름, 내용, 카테고리 번역을 제공합니다.
 * Lombok 어노테이션을 사용하여 getter, setter 등을 간편하게 생성합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-17
 * 수정일 :
 * 주요 필드 : roomMenuTransLationNum (PK), locale, roomMenuTransLationName,
 * roomMenuTransLationContent, roomMenuTransLationCategory, roomMenu (FK)
 **************************************************/

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor

public class RoomMenuTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMenuTransLationNum;

    private String locale; // 'ko', 'en' 등

    private String roomMenuTransLationName;
    private String roomMenuTransLationContent;
    private String roomMenuTransLationCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_menu_id")  // 이걸로 바꿈
    private RoomMenu roomMenu;

}
