package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.entity.RoomMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomMenuService {

    // 등록하기
    public RoomMenuDTO insert(RoomMenuDTO roomMenuDTO);

    // 목록
    public Page<RoomMenuDTO> RoomMenuList(Pageable pageable, String type, String keyword, String category);

    // 상세보기
    public RoomMenuDTO read(Long num);

    // 수정하기
    public RoomMenuDTO modify(RoomMenuDTO roomMenuDTO);

    public Page<RoomMenu> getMenuCategori(String categori, Pageable pageable);


    public void delete(Long num);
}
