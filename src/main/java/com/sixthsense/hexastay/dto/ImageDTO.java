/***********************************************
 * 클래스명 : ImageDTO
 * 기능 : ImageDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 카테고리 두 개 추가 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDTO {
    private Long imageNum;          //이미지 번호

    private String imageName;       //이미지 이름

    private String imageUrl;        //이미지 경로

    private String imageSuperEntity;//카테고리 1번 : 어디 테이블?
    private String imageSuperNum;   //카테고리 2번 : 그 중 어느 row?
}