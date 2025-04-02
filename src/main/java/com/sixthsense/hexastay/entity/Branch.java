/***********************************************
 * 클래스명 : BranchDTO
 * 기능 : BranchDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 BaseEntity 추가, 기존 날짜 필드 삭제 : 김예령
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
public class Branch  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branchNum")
    private Long branchNum;                 //번호

    @Column(name = "branchName")
    private String branchName;              //이름(상호명)

    @Column(name = "branchPhone")
    private String branchPhone;             //전화번호

    @Column(name = "branchEmail")
    private String branchEmail;             //이메일

    @Column(name = "branchAddress")
    private String branchAddress;           //주소

    @Column(name = "branchCeoName")
    private String branchCeoName;           //대표자명

    @Column(name = "centerBusinessNum")
    private String centerBusinessNum;       //사업자등록번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centerNum")
    private Center center;            //센터 참조
}