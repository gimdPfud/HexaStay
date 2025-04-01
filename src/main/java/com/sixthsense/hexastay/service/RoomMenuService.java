package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomMenuService {

    // 등록하기
    public RoomMenuDTO insert(RoomMenuDTO roomMenuDTO);

    // 목록
    public Page<RoomMenuDTO> RoomMenuList(Pageable pageable);
}
