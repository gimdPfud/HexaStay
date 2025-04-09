/***********************************************
 * 인터페이스명 : OrderstoreService
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.OrderstoreViewDTO;
import com.sixthsense.hexastay.dto.StorecartitemViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderstoreService {
    /*1. 주문 본인확인*/
    public boolean validOrder(Long orderId, String email);

    /*2. 주문하기 */
    public void insert(List<Long> itemIdList, String email);

    /*3. 주문취소*/
    public void delete(Long orderId);

    /*4. 주문내역 고객에게 보여주기*/
    Page<OrderstoreViewDTO> getOrderList(String email, Pageable pageable);
}
