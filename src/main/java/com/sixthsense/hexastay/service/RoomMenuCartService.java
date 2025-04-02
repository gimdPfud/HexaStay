package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.dto.RoomMenuDTO;

public interface RoomMenuCartService {

    public RoomMenuCartDTO roomMenuCartInsert(RoomMenuCartDTO roomMenuCartDTO);

    public RoomMenuCartDTO getCartByMemberId(Long memberNum);
}
