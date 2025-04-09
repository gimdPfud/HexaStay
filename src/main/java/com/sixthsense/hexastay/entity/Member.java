/***********************************************
 * 클래스명 : MemberDTO
 * 기능 : MemberDTO 엔티티
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
public class Member extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberNum")
    private Long memberNum;                             //번호

    @Column(name = "memberName")
    private String memberName;                          //이름

    @Column(name = "memberPassword")
    private String memberPassword;                      //비밀번호

    @Column(name = "memberPhone")
    private String memberPhone;                         //전화번호

    @Column(name = "memberEmail")
    private String memberEmail;                         //이메일



    private String memberRole;                          //시큐리티용 맴버롤


    // 🔹 ManyToOne 추가 (여러 Member가 하나의 HotelRoom을 가짐)




}
