package com.sixthsense.hexastay.service;

import com.sixthsense.hexastay.dto.CouponDTO;
import com.sixthsense.hexastay.entity.Member;

import java.util.List;

public interface CouponService {

    // 쿠폰을 생성
    public void createCoupon(CouponDTO couponDTO);

    // 쿠폰의 리스트
    List<CouponDTO> getCoupons(String email);

    // 카트의 장바구니 쿠폰 계산
    Integer getCartTotal(Member member);

    // 쿠폰 할인 적용 후 최종 결제 금액
    Integer getTotalPriceWithCoupon(String email, Long couponNum);

    // 쿠폰을 추가
    public void addToCoupon(String email);

    // 쿠폰의 반복사용을 막기 위한 매소드
    public CouponDTO applyCoupon(String memberEmail, Long couponNum);

    // 사용한 쿠폰 다시 못하게..
    boolean hasCoupon(String email, String type);

}
