/***********************************************
 * 클래스명 : StorecartitemDTO
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorecartitemDTO {
    private Long storecartitemNum;
    private Long storecartNum;
    private Long storemenuNum;
    private Integer storecartitemCount;          //장바구니에 담은 수량
    private String storemenuOptions;  //주문한 상품의 옵션들??
}