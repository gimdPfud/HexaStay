/***********************************************
 * 인터페이스명 : StoreMenuService
 * 기능 : 외부업체에서 제공하는 서비스의 처리를 담당하는 Service
 * 작성자 : 김예령
 * 작성일 : 2025-04-01
 * 수정 : 2025-04-01
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.StoreMenuDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreMenuService {
    /*등록*/
    public Long insert(StoreMenuDTO storeMenuDTO);

    /*상세보기*/
    public StoreMenuDTO read(Long pk);

    /*수정*/
    public Long modify(StoreMenuDTO storeMenuDTO);

    /*목록*/
    public Page<StoreMenuDTO> list(Pageable pageable);
    public Page<StoreMenuDTO> list(String status, Pageable pageable);
}
