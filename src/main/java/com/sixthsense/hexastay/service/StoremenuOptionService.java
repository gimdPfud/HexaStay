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

public interface StoremenuOptionService {
    /*등록*/
    public Long insert(StoremenuOptionDTO StoremenuOptionDTO) throws IOException;

    /*상세보기*/
    public StoremenuOptionDTO read(Long pk);

    /*수정*/
    public Long modify(StoremenuOptionDTO StoremenuOptionDTO) throws IOException;

    /*메뉴옵션 목록*/
    public List<StoremenuOptionDTO> list(Long storemenuNum, String status);
    public List<StoremenuOptionDTO> list(Long storemenuNum);

    /*삭제인척 하는 활성화->비활성화*/
    public Long delete(Long pk);
    /*비활성화->활성화*/
    public Long restore(Long pk);
}
