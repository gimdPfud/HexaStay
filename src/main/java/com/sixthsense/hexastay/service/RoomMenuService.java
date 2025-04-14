package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.entity.RoomMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface RoomMenuService {

    // 등록하기
    public RoomMenuDTO insert(RoomMenuDTO roomMenuDTO) throws IOException;

    // 목록
    public Page<RoomMenuDTO> RoomMenuList(Pageable pageable, String type, String keyword, String category);

    // 상세보기
    public RoomMenuDTO read(Long num);

    // 수정하기
    public RoomMenuDTO modify(RoomMenuDTO roomMenuDTO);

    // 삭제하기
    public void delete(Long num);

}


//    // 좋아요
//    public Integer roomMenuLike(Long roomMenuNum);
//
//    // 싫어요
//    public Integer roomMenuDisLike(Long roomMenuNum);
//
//    // 좋아요 취소
//    public Integer roomMenuLikeCancel(Long roomMenuNum);
//
//    // 싫어요 취소
//    public Integer roomMenuDisLikeCancel(Long roomMenuNum);
//
//    // 좋아요 수
//    public int getLikeCount (Long roomMenuNum);
//
//    // 싫어요 수
//    public int getDislikeCount(Long roomMenuNum);




