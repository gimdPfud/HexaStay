package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuOrderDTO;

import java.util.List;

public interface RoomMenuOrderService {

    // 주문 입력
    public Long roomMenuOrderInsert(RoomMenuOrderDTO roomMenuOrderDTO, String email);

    // 주문 입력 2
    public Long roomMenuOrderInsertFromCart(String email);

    // 리스트
    public List<RoomMenuOrderDTO> getOrderListByEmail(String email);
}
