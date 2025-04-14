/***********************************************
 * 클래스명 : Union
 * 기능 : Union DTO
 * 작성자 : 김부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-04-07 필드 수정 및 추가 : 김예령
 * ***********************************************/

package com.sixthsense.hexastay.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    //  경도 x
    private Double storeLongitude;
    //  위도 y 카카오맵에서는 위도,경도 순으로 받음
    private Double storeLatitude;
    private Double storeWtmX;
    private Double storeWtmY;

    //외부 업체 대표자명
    private String storeCeoName;

    //외부 업체 패스워드
    private String storePassword;

    //등록일자
    private LocalDateTime createDate;

    //수정일자
    private LocalDateTime modifyDate;

    private String storeStatus; //활성화 상태

    private String storeCategory;//가게 카테고리 (한식 중식 일식 아시안 양식 패푸 카페)

    private Double storeRating; //가게 전체 리뷰 별점의 통계... 0.0 형태로 보여주고싶음.


    private MultipartFile storeProfile; //가게 사진 이미지

    private String storeProfileMeta; //가게 사진정보

    private Long companyNum; //어디다 제공할 지 호텔(company) pk 따옴.

}
