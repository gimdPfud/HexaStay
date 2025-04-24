package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.CouponDTO;
import com.sixthsense.hexastay.entity.Coupon;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import com.sixthsense.hexastay.repository.CouponRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.repository.RoomMenuCartItemRepository;
import com.sixthsense.hexastay.repository.RoomMenuCartRepository;
import com.sixthsense.hexastay.service.CouponService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional


public class CouponServiceImpl implements CouponService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final RoomMenuCartItemRepository roomMenuCartItemRepository;
    private final RoomMenuCartRepository roomMenuCartRepository;
    private final CouponRepository couponRepository;



    @Override
    public void createCoupon(CouponDTO couponDTO) {
        log.info("쿠폰 발급 시작");
        Member member = null;
        try {
            member = memberRepository.findByMemberEmail(couponDTO.getMemberEmail());

        } catch (Exception e) {
            log.info("회원 정보를 찾는 중 오류가 발생했습니다: " + e.getMessage());
            member = null;
        }

        Coupon coupon = Coupon.builder()
                .member(member)
                .type(couponDTO.getType())
                .discountRate(couponDTO.getDiscountRate())
                .issueDate(LocalDate.now())
                .expirationDate(couponDTO.getExpirationDate())
                .isGuest(false)
                .build();

        couponRepository.save(coupon);
        log.info("쿠폰 발급 완료: {}", coupon);

    }

    @Override
    public List<CouponDTO> getCoupons(String email) {
        log.info("쿠폰 리스트 서비스 진입");

        List<Coupon> coupons = couponRepository.findByMember_MemberEmail(email);

        return coupons.stream()
                .map(coupon -> modelMapper.map(coupon, CouponDTO.class))
                .toList();
    }

    @Override
    public Integer getCartTotal(Member member) {
        log.info("장바구니 토탈 금액 쿠폰 적용 서비스 진입");
        RoomMenuCart cart = roomMenuCartRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("장바구니가 존재하지 않습니다."));

        List<RoomMenuCartItem> items = roomMenuCartItemRepository.findByRoomMenuCart(cart);

        int total = 0;
        for (RoomMenuCartItem item : items) {
            int price = item.getRoomMenu().getRoomMenuPrice();
            int qty = item.getRoomMenuCartItemAmount();
            total += price * qty;
        }

        return total;
    }

    // 쿠폰 할인 적용 후 최종 결제 금액
    @Override
    public Integer getTotalPriceWithCoupon(String email, Long couponNum) {
        log.info("장바구니 할인 적용 후 최종 결제 서비스 진입");
        Member member = memberRepository.findByMemberEmail(email);
        Coupon coupon = couponRepository.findById(couponNum)
                .orElseThrow(() -> new RuntimeException("쿠폰이 존재하지 않습니다."));

        int originalPrice = getCartTotal(member);
        int discount = (originalPrice * coupon.getDiscountRate()) / 100;

        return originalPrice - discount;
    }

    @Override
    public void addToCoupon(String email) {
        Member member = memberRepository.findByMemberEmail(email);
        Coupon coupon = Coupon.builder()
                .type("생일축하쿠폰")
                .discountRate(20)
                .member(member)
                .build();
        couponRepository.save(coupon);
    }

    @Override
    public CouponDTO applyCoupon(String memberEmail, Long couponNum) {
        log.info("다중 쿠폰 사용 서비스 진입");

        Coupon coupon = couponRepository.findByMember_MemberEmailAndCouponNum(memberEmail, couponNum);

        //  사용 제한 체크
        if (coupon.isUsed()) {
            throw new IllegalStateException("이미 사용한 쿠폰입니다.");
        }

        if (coupon.getRepeatCouponCount() != null && coupon.getRepeatCouponCount() <= 0) {
            throw new IllegalStateException("쿠폰 사용 횟수를 초과했습니다.");
        }

        //  사용 처리
        coupon.setUsed(true); // 단일 사용 쿠폰
        coupon.setUsedTime(LocalDateTime.now());
        if (coupon.getRepeatCouponCount() != null) {
            coupon.setRepeatCouponCount(coupon.getRepeatCouponCount() - 1);
        }

        couponRepository.save(coupon);

        return new CouponDTO(coupon);
    }

    @Override
    public boolean hasCoupon(String email, String type) {
        return couponRepository.existsByMember_MemberEmailAndType(email, type);
    }


}
