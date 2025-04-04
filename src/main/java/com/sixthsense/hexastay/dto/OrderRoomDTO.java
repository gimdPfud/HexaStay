package com.sixthsense.hexastay.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRoomDTO {

    private Long orderRoomNum;

    //숙박일수
    private Long orderRoomAmount;

    //호텔숙박가격
    private Long orderRoomPrice;

    //호텔 숙박가격 총 매출액
    private Long orderRoomTotalPrice;

    //매출 발생 일자
    private LocalDateTime createDate;


    //***참조 테이블 PK PK PK PK **************//
    //Room테이블 - HotelRoom
    private Long hotelRoomNum;

    private Long memberNum;
    //***참조 테이블 PK PK PK PK **************//

}
