package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor

public class StoreMenuTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeMenuTranslationNum; // 또는 storeMenuTransLationNum 등 일관성 있는 명명 규칙 사용

    private String locale; // 'ko', 'en' 등

    private String storeMenuTranslationName;    // 스토어 메뉴 이름 번역
    private String storeMenuTranslationContent; // 스토어 메뉴 내용 번역
    private String storeMenuTranslationCategory;// 스토어 메뉴 카테고리 번역

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_menu_id") // StoreMenu 엔티티의 PK를 참조하는 컬럼
    private Storemenu storeMenu;
}
