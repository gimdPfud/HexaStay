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

public class StoreTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeTranslationId; // 자체 PK

    private String locale; // 예: "en", "ja", "cn"

    private String storeName;       // 번역된 스토어 이름
    private String storeCategory;   // 번역된 스토어 카테고리
    private String storeAddress; // 주소도 번역이 필요하다면 추가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_num") // Store 엔티티의 PK (storeNum)를 참조
    private Store store; // Store 엔티티와 관계 설정

}
