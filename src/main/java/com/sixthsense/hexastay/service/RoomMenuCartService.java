package com.sixthsense.hexastay.service;
import com.sixthsense.hexastay.dto.*;
import com.sixthsense.hexastay.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Locale;


public interface RoomMenuCartService {


    // 장바구니 읽기 (read)
    public RoomMenuDTO RoomMenuCartRead(String email);

    // 장바구니 추가
    public Long RoomMenuCartInsert(String email, RoomMenuCartItemDTO roomMenuCartItemDTO);

    // 장바구니 리스트
    public Page<RoomMenuCartDetailDTO> RoomMenuCartItemList(String email, Pageable pageable);

    // 장바구니 유효성 검사 (누구 카트인가?)
    public boolean verificationRoomMenuCartItem(Long RoomMenuCartItemNum, String email);

    // 장바구니의 업데이트 수량을 업데이트 하는 매소드
    public void RoomMenuCartItemAmountUpdate(Long RoomMenuCartItemNum, Integer roomMenuCartItemAmount);

    // 장바구니를 삭제하는 매소드
    public void RoomCartMenuCartItemDelete(Long roomMenuCartItemNum);

    public RoomMenuDTO read(Long num, Locale locale, Model model);

    // 장바구니의 아이템 수를 보여주는 서비스
    public Integer getTotalCartItemCount(String memberEmail);

    // 쿠폰 적용을 위한 서비스
    public Integer getTotalPriceWithCoupon(String email, Long couponNum);

    // 장바구니 검색 후 쿠폰 적용을 위한 서비스
    public Integer getCartTotal(Member member);

    // 옵션 목록을 조회
    List<RoomMenuOptionDTO> getAvailableOptionsForProduct(Long roomMenuNum);

    // 옵션을 업데이트
    void updateCartItemWithOptions(Long cartItemId, String userEmail, List<UpdateCartOptionRequestDTO> selectedOptionUpdates); // ★ DTO 타입 변경

}
