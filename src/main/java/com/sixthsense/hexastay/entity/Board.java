/***********************************************
 * 클래스명 : BoardDTO
 * 기능 : BoardDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardNum")
    private Long boardNum;                  //번호

    @Column(name = "boardTitle")
    private String boardTitle;              //제목

    @Column(name = "boardContent")
    private String boardContent;            //내용

    @Column(name = "boardWriter")
    private String boardWriter;             //작성자

    @CreatedDate
    @Column(name = "boardCreateDate")
    private LocalDateTime boardCreateDate;  //등록일자

    @LastModifiedDate
    @Column(name = "boardModifyDate")
    private LocalDateTime boardModifyDate;  //수정일자

    @Column(name = "boardView")
    private Integer boardView;              //조회수

}