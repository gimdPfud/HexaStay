package com.sixthsense.hexastay.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
//공지,QA
public class QaDTO {
    private Long qaNum; // 번호

    private String qaTitle; // 제목

    private String qaContent; // 내용

   private String qaWriter; // 작성자

    private LocalDateTime createDate; // 등록일자

    private LocalDateTime modifyDate; // 수정일자

    private Boolean qaAnswered;

//    private Integer qaView; // 조회수

    private Long memberNum; // 회원 번호

}