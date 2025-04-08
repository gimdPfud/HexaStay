/***********************************************
 * 클래스명 : RoomServiceDTO
 * 기능 : RoomServiceDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 날짜 필드 이름 수정 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMenuDTO {

    private Long roomMenuNum;

    private String roomMenuName;                 //상품이름

    private Integer roomMenuPrice;                //상품가격

    private Integer roomMenuAmount;                 // 상품수량

    private String roomMenuContent;              //상품설명

    private String roomMenuCategory;             //카테고리

    private String roomMenuStatus;              //품절여부

    private MultipartFile roomMenuImage; // 룸 메뉴 이미지

    private String roomMenuImageMeta; //룸 메뉴 사진 정보

    private LocalDateTime createDate;    //등록일자

    private LocalDateTime modifyDate;    //수정일자

    private Long hotelRoomNum;         //방 참조

}