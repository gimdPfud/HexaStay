package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.Store;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class StoreTranslationDTO {


    private Long storeTranslationId; // 자체 PK

    private String locale; // 예: "en", "ja", "cn"

    private String storeName;       // 번역된 스토어 이름
    private String storeCategory;   // 번역된 스토어 카테고리
    private String storeAddress; // 주소도 번역이 필요하다면 추가


    private Store store; // Store 엔티티와 관계 설정

}
