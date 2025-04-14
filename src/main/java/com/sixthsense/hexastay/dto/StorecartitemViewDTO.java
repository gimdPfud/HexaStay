/***********************************************
 * 클래스명 : StorecartitemViewDTO
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import groovy.transform.Sealed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorecartitemViewDTO {
    private Long storecartitemNum;      //번호pk
    private String storemenuName;       //상품이름
    private Integer storemenuPrice;     //상품가격
    private Integer storemenuCount;     //상품개수
    private String imgName;             //이미지
}