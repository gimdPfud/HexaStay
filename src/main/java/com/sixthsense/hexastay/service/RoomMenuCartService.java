package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.entity.RoomMenuCart;

public interface RoomMenuCartService {

    // 장바구니 등록

    public RoomMenuCartDTO insertRoomMenuCart(Long memberNum, Long roomMenuNum, Integer amount);

}
