/***********************************************
 * 클래스명 : RoomServiceDTO
 * 기능 : RoomServiceDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 날짜 필드 이름 수정 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomServiceDTO {
    private Long roomServiceNum;

    private String roomServiceName;                 //이름

    private Integer roomServicePrice;                //가격

    private String roomServiceContent;              //상품설명

    private String roomServiceCategory;             //카테고리

    private boolean roomServiceStatus;              //활성화 여부

    private LocalDateTime createDate;    //등록일자

    private LocalDateTime modifyDate;    //수정일자

    private Long hotelRoomNum;         //방 참조
}