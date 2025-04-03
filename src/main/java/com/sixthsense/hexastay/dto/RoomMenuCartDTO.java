package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

/***************************************************
 *
 * 클래스명 : RoomMenuCartDTO
 * 기능 : 룸서비스 장바구니의 데이터를 전송하기 위한 DTO
 *        장바구니 정보와 그에 관련된 항목들을 포함
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-02
 * 수정일 : -
 *
 ****************************************************/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMenuCartDTO {

    private Long roomMenuCartNum; // 장바구니의 pk

    private Integer roomMenuTotalPrice;  // 장바구니의 총 가격

    private Member member;  // 사용자와 장바구니의 연관관계

    private List<RoomMenuCartItemDTO> items;  // 장바구니 항목들

    }
