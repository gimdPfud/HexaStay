/***********************************************
 * 클래스명 : Union
 * 기능 : Union 엔티티
 * 작성자 : 김부환
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
public class Unions extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unionsNum")
    private Long unionsNum;

    //외부 업체 이름
    private String unionsName;

    //외부업체 이메일
    private String unionsEmail;

    //외부업체 폰 번호
    private String unionsPhone;

    //외부 업체주소
    private String unionsAddress;

    //외부 업체 대표자명
    private String unionsCeoName;

    //외부 업체 패스워드
    private String unionsPassword;

}