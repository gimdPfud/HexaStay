/***********************************************
 * 인터페이스명 : StoremenuService
 * 기능 : 외부업체에서 제공하는 서비스의 처리를 담당하는 Service
 * 작성자 : 김예령
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.StoremenuDTO;
import com.sixthsense.hexastay.dto.StoremenuOptionDTO;

import java.io.IOException;
import java.util.List;

public interface StoremenuService {
    /*등록*/
    Long insert(StoremenuDTO storemenuDTO) throws IOException;

    /*상세보기*/
    StoremenuDTO read(Long pk);

    /*수정*/
    Long modify(StoremenuDTO storemenuDTO) throws IOException;

    /*목록*/
    List<StoremenuDTO> list(Long storeNum, String status);
    /*목록2 페이징없는 모든 목록*/
    List<StoremenuDTO> list(Long storeNum);
    /*목록3 카테고리별 목록*/
    List<StoremenuDTO> list(Long storeNum, String category, String status);

    /*삭제인척 하는 활성화->비활성화*/
    Long delete(Long pk);

    Long soldout(Long pk);
    /*비활성화->활성화*/
    Long restore(Long pk);
}
