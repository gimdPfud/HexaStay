/***********************************************
 * 클래스명 : BranchDTO
 * 기능 : BranchDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branchNum")
    private Long branchNum;                 //번호

    @Column(name = "branchName")
    private String branchName;              //이름

    @Column(name = "branchPhone")
    private String branchPhone;             //전화번호

    @Column(name = "branchEmail")
    private String branchEmail;             //이메일

    @Column(name = "branchAddress")
    private String branchAddress;           //주소

    @Column(name = "branchCeoName")
    private String branchCeoName;           //대표명

    @Column(name = "branchCreateDate")
    @CreatedDate
    private LocalDateTime branchCreateDate; //등록일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centerNum")
    private Center branchCenter;            //센터 참조
}