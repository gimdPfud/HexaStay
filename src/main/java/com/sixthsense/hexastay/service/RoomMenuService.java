package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public interface RoomMenuService {

    // 등록하기
    public RoomMenuDTO insert(RoomMenuDTO roomMenuDTO) throws IOException;

    // 목록
    public Page<RoomMenuDTO> RoomMenuList(Pageable pageable, String type, String keyword, String category, Locale locale, boolean forUserView);

    // 상세보기
    public RoomMenuDTO read(Long num, Locale locale);

    // 수정하기
    public RoomMenuDTO modify(RoomMenuDTO roomMenuDTO);

    // 삭제하기
    public void delete(Long num);

    // 번역
    List<RoomMenuDTO> getMenusWithLocale(String locale);

    public Page<RoomMenuDTO> searchRoomMenuList(Pageable pageable, String type, String keyword, String category, Locale locale);


}




