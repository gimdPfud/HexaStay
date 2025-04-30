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
    private final CouponMailService couponMailService;



    public void createCoupon(CouponDTO couponDTO) {
        log.info("쿠폰 발급 시작");

        Member member = memberRepository.findByMemberEmail(couponDTO.getMemberEmail());
        if (member == null) {
            log.warn("존재하지 않는 회원 이메일입니다: {}", couponDTO.getMemberEmail());
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        CouponType typeEnum = couponDTO.getCouponType(); // DTO에서 Enum 값 가져옴
        if (typeEnum == null) {
            log.error("쿠폰 타입이 DTO에 설정되지 않았습니다.");
            throw new IllegalArgumentException("쿠폰 타입 정보가 누락되었습니다.");
        }

        // >>> **발급 제한 체크 로직 (이전 단계에서 추가한 코드)** <<<
        int maxCount = typeEnum.getMaxCount();
        if (maxCount > 0) {
            long issuedCount = couponRepository.countByMemberAndCouponType(member, typeEnum);
            log.info("쿠폰 발급 요청 - 회원: {}, 타입: {}, 최대 발급: {}, 현재 발급된 수: {}",
                    member.getMemberEmail(), typeEnum.name(), maxCount, issuedCount);
            if (issuedCount >= maxCount) {
                log.warn("쿠폰 발급 제한 초과 - 회원: {}, 타입: {}", member.getMemberEmail(), typeEnum.name());
                throw new IllegalStateException(typeEnum.name() + " 쿠폰은 최대 " + maxCount + "개까지 발급 가능합니다.");
            }
        } else {
            log.info("쿠폰 발급 요청 - 회원: {}, 타입: {}, 발급 제한 없음 (maxCount=0)",
                    member.getMemberEmail(), typeEnum.name());
        }
        // >>> **발급 제한 체크 로직 끝** <<<


        // 발급 제한에 걸리지 않았다면 쿠폰 생성 및 저장 진행
        Coupon coupon = Coupon.builder()
                .member(member)
                .couponType(typeEnum) // Enum 값 그대로 사용
                // >>> **수정: 할인율을 DTO가 아닌 CouponType enum에서 가져옴** <<<
                .discountRate(typeEnum.getDiscountRate()) // <<< CouponType enum에서 해당 타입의 할인율 가져옴
                // >>> **수정 완료** <<<
                .issueDate(LocalDate.now()) // 발급일자는 서버에서 생성
                .expirationDate(couponDTO.getExpirationDate()) // 만료일 (DTO에서 받거나 백엔드에서 고정 고려)
                // isGuest, used, repeatCouponCount, usedTime 등 DTO에 있다면 builder로 전달
                .isGuest(couponDTO.isGuest())
                .used(couponDTO.isUsed())
                .repeatCouponCount(couponDTO.getRepeatCouponCount())
                .usedTime(couponDTO.getUsedTime())
                // ... 다른 필드들 ...
                .build();

        log.info("쿠폰 엔티티 생성 확인 직전: {}", coupon); // 엔티티 저장 직전 로그


        // === ✨ 쿠폰 메일 발송 ===
        try {
            couponMailService.sendCouponEmail(
                    member.getMemberEmail(),
                    coupon.getCouponType().name(),                         // 또는 coupon.getCouponCode() 사용
                    coupon.getDiscountRate(),
                    coupon.getExpirationDate()
            );
            log.info("✅ 쿠폰 메일 발송 성공 - 수신자: {}", member.getMemberEmail());
        } catch (Exception e) {
            log.error("❌ 쿠폰 메일 발송 실패 - 수신자: {}, 오류: {}", member.getMemberEmail(), e.getMessage());
        }
        couponRepository.save(coupon); // 쿠폰 저장
        log.info("쿠폰 발급 완료: {}", coupon); // 발급 완료 로그
        }

    @Override
    public List<CouponDTO> getCoupons(String email) {
        log.info("쿠폰 리스트 서비스 진입 - email: {}", email); // 로그에 email 추가

        List<Coupon> coupons = couponRepository.findUsableCouponsByMemberEmail(email);
        log.info("사용 가능한 쿠폰 {}개 조회됨.", coupons.size()); // 조회된 쿠폰 개수 로그

        // Coupon 엔티티 목록을 CouponDTO 목록으로 변환
        return coupons.stream()
                .map(coupon -> { // 'coupon'은 Coupon 엔티티 객체
                    CouponDTO dto = modelMapper.map(coupon, CouponDTO.class);

                    // DTO의 couponType 필드에 Enum 값이 잘 담겼는지 확인하는 로그 (선택 사항)
                    log.info("Mapped CouponType for DTO {}: {}", dto.getCouponNum(), dto.getCouponType());

                    return dto; // 매핑된 DTO 반환
                })
                .toList(); // List<CouponDTO>로 수집하여 반환
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
        log.info("사용된 쿠폰 서비스 진입 - email: {}, type: {}", email, type);
        return couponRepository.existsByMember_MemberEmailAndCouponType(email, type);
    }


}
