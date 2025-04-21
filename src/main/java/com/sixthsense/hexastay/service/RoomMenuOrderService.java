package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuOrderDTO;

import java.util.List;

public interface RoomMenuOrderService {

    // 주문 입력
    public Long roomMenuOrderInsert(RoomMenuOrderDTO roomMenuOrderDTO, String email);

    // 주문 입력 2
    public Long roomMenuOrderInsertFromCart(String email, String requestMessage);

    // 리스트
    public List<RoomMenuOrderDTO> getOrderListByEmail(String email);

    // 주문취소
    public void cancelRoomMenuOrder(Long orderNum, String email);

    // 판매자가 주문 정보를 확인
    public List<RoomMenuOrderDTO> getAllOrdersForAdmin();

    // 관리자에게 알람을 띄우기
    public void RoomMenuSendOrderAlert(RoomMenuOrderDTO orderDto);


}
