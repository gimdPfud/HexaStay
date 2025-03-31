package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.RoomService;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderServiceDTO {


    private Long orderServiceNum;

    //룸서비스 판매수량
    private Long orderServiceAmount;

    //룸서비스 상품가격
    private Long orderServicePrice;

    //룸서비스 총매출
    private Long orderServiceTotalPrice;

    //룸서비스 결재
    private String orderServicePay;

    //룸서비스 발생일자
    private LocalDateTime orderServiceCreateDate;

    //룸서비스 수정일자
    private LocalDateTime orderServiceModifyDate;

    //*****참조테이블*************
    //룸서비스 참조(RoomService)
    private Long roomServiceNum;

}
