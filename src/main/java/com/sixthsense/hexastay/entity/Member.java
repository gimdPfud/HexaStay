/***********************************************
 * 클래스명 : MemberDTO
 * 기능 : MemberDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/
package com.sixthsense.hexastay.entity;

import com.sixthsense.hexastay.constant.Gender;
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
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberNum")
    private Long memberNum;                             //번호

    @Column(name = "memberPassword")
    private String memberPassword;                      //비밀번호

    @Column(name = "memberPhone")
    private String memberPhone;                         //전화번호

    @Column(name = "memberEmail")
    private String memberEmail;                         //이메일

    @Column(name = "memberBirth")
    private String memberBirth;                         //생년월일

    @Enumerated(EnumType.STRING)
    @Column(name = "memberGender")
    private Gender memberGender;                        //성별

    @CreatedDate
    @Column(name = "memberCreateDate")
    private LocalDateTime memberCreateDate;             //등록일자
}
