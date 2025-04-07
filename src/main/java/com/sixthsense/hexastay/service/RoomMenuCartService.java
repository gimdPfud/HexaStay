package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomMenuCartService {

    // 장바구니 등록

    public RoomMenuCartDTO insertRoomMenuCart(Long memberNum, Long roomMenuNum, Integer amount);

    public Page<RoomMenuDTO> RoomMenuList(Pageable pageable, String type, String keyword, String category);

}
