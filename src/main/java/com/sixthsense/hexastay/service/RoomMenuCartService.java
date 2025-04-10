package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.dto.RoomMenuCartDetailDTO;
import com.sixthsense.hexastay.dto.RoomMenuCartItemDTO;
import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.entity.RoomMenu;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomMenuCartService {

    // 장바구니 등록

//    public RoomMenuCartDTO insertRoomMenuCart(Long memberNum, Long roomMenuNum, Integer amount);

    // 리스트
    public Page<RoomMenuDTO> RoomMenuList(Pageable pageable, String type, String keyword, String category);

    // 장바구니 읽기 (read)
    public RoomMenuDTO RoomMenuCartRead(String email);

    // ?
    public RoomMenuCartDTO getCartByMember(Long memberNum);

    // 장바구니 추가
    public Long RoomMenuCartInsert(String email, RoomMenuDTO roomMenuDTO);

    // 장바구니 리스트
    public Page<RoomMenuCartDetailDTO> RoomMenuCartItemList(String email, Pageable pageable);

    // 장바구니 유효성 검사 (누구 카트인가?)
    public boolean verificationRoomMenuCartItem(Long RoomMenuCartItemNum, String email);

    // 장바구니의 업데이트 수량을 업데이트 하는 매소드
    public void RoomMenuCartItemAmountUpdate(Long RoomMenuCartItemNum, Integer roomMenuCartItemAmount);

    // 장바구니를 삭제하는 매소드
    public void RoomCartMenuCartItemDelete(Long roomMenuCartItemNum);

    public RoomMenuDTO read(Long num);

}
