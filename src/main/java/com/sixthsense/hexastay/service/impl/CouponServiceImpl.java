package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.CouponDTO;
import com.sixthsense.hexastay.entity.Coupon;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.entity.RoomMenuCart;
import com.sixthsense.hexastay.entity.RoomMenuCartItem;
import com.sixthsense.hexastay.enums.CouponType;
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

        Member member = memberRepository.findByMemberEmail(couponDTO.getMemberEmail());

        if (member == null) {
            log.warn("존재하지 않는 회원 이메일입니다: {}", couponDTO.getMemberEmail());
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        Coupon coupon = Coupon.builder()
                .member(member)
                .couponType(couponDTO.getCouponType())
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

        List<Coupon> coupons = couponRepository.findUsableCouponsByMemberEmail(email);

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

            total += item.getRoomMenuCartItemPrice();

        }

        return total;
    }

    // 쿠폰 할인 적용 후 최종 결제 금액
    @Override
    public Integer getTotalPriceWithCoupon(String email, Long couponNum) {
        log.info("장바구니 할인 적용 후 최종 결제 서비스 진입");
        Member member = memberRepository.findByMemberEmail(email);
        Coupon coupon = couponRepository.findById(couponNum)
                .orElseThrow(() -> new RuntimeException("쿠폰을 찾을 수 없습니다."));

        // 1. 유효성 검사 (만료, 사용 여부 등은 여기서 체크합니다.)
        if (!coupon.getMember().getMemberEmail().equals(email)) {
            throw new RuntimeException("해당 쿠폰은 이 회원의 것이 아닙니다.");
        }

        if (coupon.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("쿠폰이 만료되었습니다.");
        }

        // **수정:** 쿠폰이 이미 사용된 쿠폰인지 체크합니다. (getCoupons 쿼리로 걸러지지만, 혹시 모르니 한 번 더 체크 가능)
        if (coupon.isUsed()) {
            // 반복 사용 쿠폰인데 횟수가 0이하라면 사용 불가
            if (coupon.getRepeatCouponCount() == null || coupon.getRepeatCouponCount() <= 0) {
                throw new RuntimeException("이미 사용된 쿠폰입니다.");
            }
            // 반복 사용 쿠폰이고 횟수가 남았다면 사용 가능
        }


        // 2. 할인 금액 계산
        int originalPrice = getCartTotal(member); // 수정된 getCartTotal 사용 (옵션 포함)
        int discount = (originalPrice * coupon.getDiscountRate()) / 100;
        int finalPrice = originalPrice - discount;

        // >>> **여기서 쿠폰의 used 상태를 true로 바꾸거나 save()를 호출하는 코드를 모두 삭제합니다.** <<<
        // 쿠폰 사용 처리는 실제 주문 완료 로직에서 진행합니다.


        log.info("쿠폰 적용 결과: 원 가격 {}, 할인 {}, 최종 {}", originalPrice, discount, finalPrice);

        return finalPrice; // 계산된 최종 금액만 반환
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
    public boolean hasCoupon(String email, CouponType type) {
        return couponRepository.existsByMember_MemberEmailAndCouponType(email, type);
    }


}
