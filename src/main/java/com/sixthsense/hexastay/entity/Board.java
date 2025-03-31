/***********************************************
 * 클래스명 : BoardDTO
 * 기능 : BoardDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31, BaseEntity 추가, 기존 날짜 필드 삭제 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board  extends BaseEntity {
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

    @Column(name = "boardView")
    private Integer boardView;              //조회수

}