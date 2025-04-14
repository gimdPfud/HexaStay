/***********************************************
 * 인터페이스명 : OrderstoreService
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-04-08
 * 수정 : 2025-04-08
 * ***********************************************/
package com.sixthsense.hexastay.service;


import com.sixthsense.hexastay.dto.OrderstoreDTO;
import com.sixthsense.hexastay.dto.OrderstoreViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderstoreService {
    /*1. 주문 본인확인*/
    public boolean validOrder(Long orderId, String email);

    /*2. 주문하기 */
    public int insert(List<Long> itemIdList, String email);

    /*3. 주문취소*/
    public void cancel(Long orderId);
    /*3-1. 주문완료*/
    public void end(Long orderId);

    /*4. 고객용 주문내역 목록*/
    Page<OrderstoreViewDTO> getOrderList(String email, Pageable pageable);

    /*5. 매출용 주문 목록? : 완료된 주문들만 전부 DTO리스트로 내보내는 메소드*/
    List<OrderstoreDTO> getAllList();
    /*6. 스토어관리자용 : 들어온 주문 내역만 확인하기. (근데 진짜 이걸 만들어야한다고? 그냥 호텔관리시스템인데?)*/
    List<OrderstoreDTO> getOrderedList(Long storeNum);
}
