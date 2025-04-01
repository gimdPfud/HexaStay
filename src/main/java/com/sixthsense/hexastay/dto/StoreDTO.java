/***********************************************
 * 클래스명 : Union
 * 기능 : Union DTO
 * 작성자 : 김부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-03-31 날짜 필드 이름 수정 : 김예령
 * ***********************************************/

package com.sixthsense.hexastay.dto;

import java.time.LocalDateTime;



public class StoreDTO {


    private Long storeNum;

    //외부 업체 이름
    private String storeName;

    //외부업체 이메일
    private String storeEmail;

    //외부업체 폰 번호
    private String storePhone;

    //외부 업체주소
    private String storeAddress;

    //외부 업체 대표자명
    private String storeCeoName;

    //외부 업체 패스워드
    private String storePassword;

    //등록일자
    private LocalDateTime createDate;

    //수정일자
    private LocalDateTime modifyDate;

}
