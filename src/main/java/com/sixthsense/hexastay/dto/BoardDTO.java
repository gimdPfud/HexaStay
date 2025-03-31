/***********************************************
 * 클래스명 : BoardDTO
 * 기능 : BoardDTO
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
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
public class BoardDTO {
    private Long boardNum;                  //번호

    private String boardTitle;              //제목

    private String boardContent;            //내용

    private String boardWriter;             //작성자

    private LocalDateTime boardCreateDate;  //등록일자

    private LocalDateTime boardModifyDate;  //수정일자

    private Integer boardView;              //조회수

}