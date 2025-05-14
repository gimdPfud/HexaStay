/***********************************************
 * 클래스명 : StoremenuOptionDTO
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-10
 * 수정 : 2025-04-10
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.StoremenuOption;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StoremenuOptionDTO {
    private Long storemenuOptionNum;        //옵션pk
    @NotBlank
    @Size(min = 1, max = 20)
    private String storemenuOptionName;     //옵션 이름
    @Min(0)
    private Integer storemenuOptionPrice;   //옵션 가격
    private String storemenuOptionStatus;   //활성화비활성화
    private Long storemenuNum;            //어느 메뉴의 옵션인지?? 연결

    public StoremenuOptionDTO(StoremenuOption option) {
        this.storemenuOptionNum = option.getStoremenuOptionNum();
        this.storemenuOptionName = option.getStoremenuOptionName();
        this.storemenuOptionPrice = option.getStoremenuOptionPrice();
        this.storemenuOptionStatus = option.getStoremenuOptionStatus();
        this.storemenuNum = option.getStoremenu().getStoremenuNum();
    }
}