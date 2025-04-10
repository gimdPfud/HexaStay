/***********************************************
 * 클래스명 : StoremenuDTO
 * 기능 : StoremenuDTO
 * 작성자 : 김부환
 * 작성일 : 2025-03-31
 * 수정 : 2025-04-01 이름 수정 : 김예령
 * ***********************************************/
package com.sixthsense.hexastay.dto;


import com.sixthsense.hexastay.entity.StoremenuOption;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoremenuDTO {

    private Long storemenuNum;

    //룸서비스외부 업체 상품이름
    private String storemenuName;

    //룸서비스외부 업체상품 가격
    private Integer storemenuPrice;

    //룸서비스외부 업체상품 설명
    private String storemenuContent;

    //룸서비스외부 업체상품 카테고리
    private String storemenuCategory;

    //룸서비스외부 업체상품 서비스 활성화 여부
    private String storemenuStatus;

    //룸서비스외부 등록일
    private LocalDateTime createDate;

    //룸서비스외부 수정일
    private LocalDateTime modifyDate;

    //참조 테이블 - Store 테이블 Pk 참조
    private Long storeNum;

    private MultipartFile storemenuImg; //가게 사진 이미지

    private String storemenuImgMeta; //가게 사진정보

    private List<StoremenuOptionDTO> storemenuOptionDTOList = new ArrayList<>();
}