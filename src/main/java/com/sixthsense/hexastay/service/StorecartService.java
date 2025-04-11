/***********************************************
 * 인터페이스명 : StorecartService
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.StorecartitemDTO;
import com.sixthsense.hexastay.dto.StorecartitemViewDTO;

import java.util.List;

public interface StorecartService {
    /*1. 등록*/
    public Long addCart(StorecartitemDTO dto, String email);

    /*2. 목록*/
    public List<StorecartitemViewDTO> getCartList(String email);

    /*3. 카트 주인 찾기*/
    public boolean validCartItemOwner(Long storeCartItemId, String email);

    /*4. 수정 (카트 수량 변경)*/
    public Integer updateCount(Long storeCartItemId, Integer count);

    /*5. 카트 삭제*/
    public void deleteCartItem(Long storeCartItemId);
}
