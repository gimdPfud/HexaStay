/***********************************************
 * 클래스명 : StoremenuOptionDTO
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-10
 * 수정 : 2025-04-10
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.Storemenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoremenuOptionDTO {
    private Long storemenuOptionNum;        //옵션pk
    private String storemenuOptionName;     //옵션 이름
    private Integer storemenuOptionPrice;   //옵션 가격
    private String storemenuOptionStatus;   //활성화비활성화
    private Storemenu storemenu;            //어느 메뉴의 옵션인지?? 연결
}
