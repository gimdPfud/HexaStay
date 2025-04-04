/***********************************************
 * 클래스명 : MemberDTO
 * 기능 : MemberDTO 엔티티
 * 작성자 : 김예령
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 날짜 필드 이름 수정 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.constant.Gender;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    private Long memberNum;                             //번호

    private String memberName;                          //이름

    private String memberPassword;                      //비밀번호

    private String memberPhone;                         //전화번호

    private String memberEmail;                         //이메일

    private String memberBirth;                         //생년월일


    private LocalDateTime createDate;       //가입 일자





}
