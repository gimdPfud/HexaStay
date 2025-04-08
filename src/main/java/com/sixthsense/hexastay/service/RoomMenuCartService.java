package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.RoomMenuCartDTO;
import com.sixthsense.hexastay.dto.RoomMenuCartItemDTO;
import com.sixthsense.hexastay.dto.RoomMenuDTO;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomMenuCartService {

    // 장바구니 등록

//    public RoomMenuCartDTO insertRoomMenuCart(Long memberNum, Long roomMenuNum, Integer amount);

    public Page<RoomMenuDTO> RoomMenuList(Pageable pageable, String type, String keyword, String category);

    public RoomMenuCartDTO getCartByMember(Long memberNum);

    // 장바구니 추가
    public Long RoomMenuCartInsert(Long roomMenuCartNum, String email, RoomMenuCartItemDTO roomMenuCartItemDTO);

    // 장바구니 리스트
    public Page<RoomMenuCartItemDTO> RoomMenuCartItemList(String email, Pageable pageable);

    // 장바구니 유효성 검사 (누구 카트인가?)
    public boolean verificationRoomMenuCartItem(Long RoomMenuCartItemNum, String email);

    // 장바구니의 업데이트 수량을 업데이트 하는 매소드
    public void updateRoomMenuCartItemAmount(Long RoomMenuCartItemNum, Integer roomMenuCartItemAmount);

    public void RoomCartMenuCartItemDelete(Long roomMenuCartItemNum);

}
