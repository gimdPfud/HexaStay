/***********************************************
 * 클래스명 : Union
 * 기능 : Union DTO
 * 작성자 : 김부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31
 * ***********************************************/

package com.sixthsense.hexastay.dto;

import java.time.LocalDateTime;



public class UniosDTO {


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

    //등록일자
    private LocalDateTime unionCreateDate;

    //수정일자
    private LocalDateTime unionModifyDate;

}
