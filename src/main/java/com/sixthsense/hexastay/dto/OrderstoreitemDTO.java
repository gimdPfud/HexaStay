/********************************************
 * 클래스명 : OrderStoreitemDTO
 * 기능 : OrderStoreitemDTO
 * 작성자 : 김예령
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderstoreitemDTO {

    private Long orderstoreitemNum; //pk

    private Long orderstoreNum;
    private StoremenuDTO storemenuDTO;

    @NotNull
    @Min(1)
    private Long orderstoreitemAmount;
    private Long orderstoreitemPrice;
    private Long orderstoreitemTotalPrice;

    private String storemenuOptions;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;


}
