package com.sixthsense.hexastay.dto;

// ★ 파일명: UpdateCartOptionRequestDTO.java (또는 유사한 이름) ★

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateCartOptionRequestDTO { // ★ 클래스명 변경

    private Long roomMenuOptionNum; // JavaScript의 "roomMenuOptionNum" 키와 일치

    private Integer roomMenuCartItemOptionAmount; // JavaScript의 "roomMenuCartItemOptionAmount" 키와 일치
}