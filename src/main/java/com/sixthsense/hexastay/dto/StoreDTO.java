/***********************************************
 * 클래스명 : Union
 * 기능 : Union DTO
 * 작성자 : 김부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-04-07 필드 수정 및 추가 : 김예령
 * ***********************************************/

package com.sixthsense.hexastay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Size(min = 1, max = 20)
    private String storeName;
    @NotBlank
    private String storePhone;

    @NotBlank
    private String storeAddress;        //외부 업체주소
    private Double storeLongitude;      //  경도 x
    private Double storeLatitude;       //  위도 y 카카오맵에서는 위도,경도 순으로 받음
    private Double storeWtmX;
    private Double storeWtmY;

    private String storeCeoName;
    private String storePassword;
    private String storeStatus; //활성화 상태
    @NotBlank
    private String storeCategory;//가게 카테고리 (한식 중식 일식 아시안 양식 패푸 카페)

    private MultipartFile storeProfile; //가게 사진 이미지
    private String storeProfileMeta; //가게 사진정보

    @NotNull
    private Long companyNum; //어디다 제공할 지 호텔(company) pk 따옴.
    private String companyName; //어디다 제공할 지 호텔(company) 이름.

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
}
