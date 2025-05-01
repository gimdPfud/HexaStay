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

import java.security.Principal;
import java.util.List;

public interface StorecartService {

    /*1. 등록*/
    int addCart(StorecartitemDTO dto, Long roomNum);

    /*2. 목록*/
    List<StorecartitemViewDTO> getCartList(Long roomNum);
    Long getItemCount(Long roomNum);

    /*3. 카트 주인 찾기*/
    boolean validCartItemOwner(Long storeCartItemId, Long roomNum);

    /*4. 수정 (카트 수량 변경)*/
    Integer updateCount(Long storeCartItemId, Integer count);

    /*5. 카트 삭제*/
    void deleteCartItem(Long storeCartItemId);

    /*6. 카트 비우기*/
    void clearCartItems(Long roomNum);
}
