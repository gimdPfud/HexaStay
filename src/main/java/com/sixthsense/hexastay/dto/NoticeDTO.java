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
public class NoticeDTO {
    private Long noticeNum; // 번호

    private String noticeTitle; // 제목

    private String noticeContent; // 내용

//    private String noticeWriter; // 작성자

    private LocalDateTime createDate; // 등록일자

    private LocalDateTime modifyDate; // 수정일자

//    private Integer noticeView; // 조회수

//    private Long memberNum; // 관리자

}