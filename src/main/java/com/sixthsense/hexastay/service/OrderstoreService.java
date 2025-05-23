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
import java.util.Locale;

public interface OrderstoreService {
    /*1. 주문 본인확인*/
//    boolean validOrder(Long orderId, String email);

    /*2. 주문하기 */
//    int insert(List<Long> itemIdList, String email, String orderstoreMessage);
    int insert(List<Long> itemIdList, Long roomNum, String orderstoreMessage);

    Long getLastOrder(Long roomNum);
    void end(Long orderId);
    void cancel(Long orderId);

    /*4. 고객용 주문내역 목록*/
//    List<OrderstoreViewDTO> getOrderList(String email);
    List<OrderstoreViewDTO> getOrderList(Long roomNum);
    Page<OrderstoreViewDTO> getOrderList(Long roomNum, Pageable pageable, Locale locale);

    /*5. 매출용 주문 목록? : 완료된 주문들만 전부 DTO리스트로 내보내는 메소드*/
    List<OrderstoreDTO> getAllList();
    /*6. 스토어관리자용 : 들어온 주문 내역만 확인하기. (근데 진짜 이걸 만들어야한다고? 그냥 호텔관리시스템인데?)*/
    List<OrderstoreDTO> getOrderedList(Long storeNum);
}
