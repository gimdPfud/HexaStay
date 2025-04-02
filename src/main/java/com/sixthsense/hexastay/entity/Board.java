package com.sixthsense.hexastay.entity;
import com.sixthsense.hexastay.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Board  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //번호
    @Column(name = "boardNum")
    private Long boardNum;                  //번호
    //제목
    @Column(name = "boardTitle")
    private String boardTitle;              //제목
    //내용
    @Column(name = "boardContent")
    private String boardContent;            //내용
    //작성자
    @Column(name = "boardWriter")
    private String boardWriter;             //작성자
    //뷰
    @Column(name = "boardView")
    private Integer boardView;
    //생성일
    @CreatedDate
    @Column(name = "createDate",nullable = false)
    private LocalDateTime createDate;
    //수정일
    @LastModifiedDate
    @Column(name = "medifyDate")
    private LocalDateTime modifyDate;
    //가져오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberNum")
    private Member member;

}