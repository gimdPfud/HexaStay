/***********************************************
 * 인터페이스명 : StoremenuService
 * 기능 : 외부업체에서 제공하는 서비스의 처리를 담당하는 Service
 * 작성자 : 김예령
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.StoremenuDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoremenuService {
    /*등록*/
    public Long insert(StoremenuDTO storemenuDTO);

    /*상세보기*/
    public StoremenuDTO read(Long pk);

    /*수정*/
    public Long modify(StoremenuDTO storemenuDTO);

    /*목록*/
    public Page<StoremenuDTO> list(Long storeNum, String status, Pageable pageable);

    /*삭제인척 하는 활성화->비활성화*/
    public void delete(Long pk);
}
