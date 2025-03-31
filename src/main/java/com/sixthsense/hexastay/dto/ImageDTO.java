/***********************************************
 * 클래스명 : ImageDTO
 * 기능 : ImageDTO 엔티티 (todo 추후 수정)
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
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
    private Long imageNum;

    private String imageName;       //이미지 이름

    private String imageUrl;        //이미지 경로

    /*나중에 참조*/
}