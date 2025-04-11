package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuOrderDTO;

public interface RoomMenuOrderService {

    // 주문 입력
    public Long roomMenuOrderInsert(RoomMenuOrderDTO roomMenuOrderDTO, String email);
}
