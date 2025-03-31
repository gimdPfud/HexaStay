/***********************************************
 * 클래스명 : ImageDTO
 * 기능 : ImageDTO 엔티티 (todo 추후 수정)
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 카테고리 컬럼 두 개 추가 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imageNum")
    private Long imageNum;

    @Column(name = "imageName")
    private String imageName;       //이미지 이름

    @Column(name = "imageUrl")
    private String imageUrl;        //이미지 경로

    @Column(name = "imageSuperEntity")
    private String imageSuperEntity;//카테고리 1번 : 어디 테이블?
    @Column(name = "imageSuperNum")
    private String imageSuperNum;   //카테고리 2번 : 그 중 어느 row?
}