package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.Storemenu;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class StoreMenuTranslationDTO {

    private Long storeMenuTranslationNum; // 또는 storeMenuTransLationNum 등 일관성 있는 명명 규칙 사용

    private String locale; // 'ko', 'en' 등

    private String storeMenuTranslationName;    // 스토어 메뉴 이름 번역
    private String storeMenuTranslationContent; // 스토어 메뉴 내용 번역
    private String storeMenuTranslationCategory;// 스토어 메뉴 카테고리 번역

    private Storemenu storeMenu;
}
