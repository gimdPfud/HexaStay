package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuOptionDTO;

import java.util.List;

public interface RoomMenuOptionService {

    // 조회
    public List<RoomMenuOptionDTO> roomMenuOptionAllList(Long roomMenuNum);

    // 옵션을 만들기
    public RoomMenuOptionDTO roomMenuOptionInsert(Long roomMenuNum, RoomMenuOptionDTO roomMenuOptionDTO);

    // 옵션을 업데이트
    public RoomMenuOptionDTO roomMenuOptionUpdate(Long roomMenuOptionNum, RoomMenuOptionDTO roomMenuOptionDTO);

    // 옵션을 삭제
    public void roomMenuOptionDelete(Long roomMenuOptionNum);





}