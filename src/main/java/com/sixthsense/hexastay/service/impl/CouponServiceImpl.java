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

/**************************************************
 * 클래스명 : CouponServiceImpl
 * 기능   : 쿠폰 관련 비즈니스 로직을 처리하는 서비스 구현 클래스입니다. (CouponService 인터페이스 구현)
 * 쿠폰 생성, 회원의 사용 가능한 쿠폰 목록 조회, 장바구니 총액 계산, 쿠폰 적용 시 할인된 최종 가격 계산,
 * 특정 타입의 쿠폰 소지 여부 확인 등의 기능을 제공합니다.
 * 쿠폰 생성 시 비동기 이메일 발송 서비스(CouponMailService)를 호출합니다.
 * 모든 public 메소드는 기본적으로 트랜잭션 내에서 실행됩니다 (@Transactional 클래스 레벨).
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-24
 * 수정일 : 2025-05-09
 * 주요 메소드/기능 : createCoupon, getCoupons, getCartTotal, getTotalPriceWithCoupon,
 * applyCoupon, hasCoupon
 **************************************************/

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

    /**************************************************
     * 메소드명 : createCoupon
     * 신규 쿠폰 생성 및 발급
     * 기능: 전달받은 `CouponDTO` 정보를 기반으로 신규 쿠폰을 생성하고 데이터베이스에 저장합니다.
     * 회원의 존재 여부, 쿠폰 타입 유효성, 발급 제한 수량을 체크합니다.
     * 성공적으로 쿠폰이 생성되면 비동기적으로 쿠폰 발급 안내 메일을 발송합니다.
     * @param couponDTO CouponDTO : 생성할 쿠폰의 정보(회원 이메일, 쿠폰 타입, 만료일 등)를 담은 DTO.
     * @throws IllegalArgumentException : 존재하지 않는 회원이거나 쿠폰 타입 정보가 누락된 경우.
     * @throws IllegalStateException : 쿠폰 발급 제한 수량을 초과한 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-24
     * 수정일 : -
     **************************************************/

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

        // 발급 제한에 걸리지 않았다면 쿠폰 생성 및 저장 진행
        Coupon coupon = Coupon.builder()
                .member(member)
                .couponType(typeEnum) // Enum 값 그대로 사용
                .discountRate(typeEnum.getDiscountRate()) // <<< CouponType enum에서 해당 타입의 할인율 가져옴
                .issueDate(LocalDate.now()) // 발급일자는 서버에서 생성
                .expirationDate(couponDTO.getExpirationDate()) // 만료일 (DTO에서 받거나 백엔드에서 고정 고려)
                .isGuest(couponDTO.isGuest())
                .used(couponDTO.isUsed())
                .repeatCouponCount(couponDTO.getRepeatCouponCount())
                .usedTime(couponDTO.getUsedTime())
                .build();

        log.info("쿠폰 엔티티 생성 확인 직전: {}", coupon); // 엔티티 저장 직전 로그


        // === ✨ 쿠폰 메일 발송 ===
        try {
            couponMailService.sendCouponEmail(
                    member.getMemberEmail(),
                    coupon.getCouponType().name(),
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

    /**************************************************
     * 메소드명 : getCoupons
     * 사용 가능한 쿠폰 목록 조회
     * 기능: 특정 회원(`email`)이 현재 사용할 수 있는 모든 쿠폰 목록을 조회하여 `CouponDTO` 리스트 형태로 반환합니다.
     * @param email String : 쿠폰 목록을 조회할 회원의 이메일.
     * @return List<CouponDTO> : 조회된 사용 가능한 쿠폰 DTO 목록.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-24
     * 수정일 : 2025-04-29
     **************************************************/

    @Override
    public List<CouponDTO> getCoupons(String email) {
        log.info("쿠폰 리스트 서비스 진입 - email: {}", email); // 로그에 email 추가

        List<Coupon> coupons = couponRepository.findUsableCouponsByMemberEmail(email);

        // Coupon 엔티티 목록을 CouponDTO 목록으로 변환
        return coupons.stream()
                .map(coupon -> { // 'coupon'은 Coupon 엔티티 객체
                    CouponDTO dto = modelMapper.map(coupon, CouponDTO.class);

                    log.info("매핑된 쿠폰 타입의 DTO {}: {}", dto.getCouponNum(), dto.getCouponType());

                    return dto; // 매핑된 DTO 반환
                })
                .toList();
    }

    /**************************************************
     * 메소드명 : getCartTotal
     * 장바구니 총액 계산
     * 기능: 특정 회원(`member`)의 장바구니에 담긴 모든 상품들의 총액을 계산하여 반환합니다.
     * 현재 로직은 각 장바구니 항목의 `roomMenuCartItemPrice`와 `roomMenuCartItemCount`를 곱하여 합산합니다.
     * @param member Member : 장바구니 총액을 계산할 회원 엔티티.
     * @return Integer : 계산된 장바구니 총액.
     * @throws RuntimeException : 해당 회원의 장바구니가 존재하지 않는 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-24
     * 수정일 : -
     **************************************************/

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

    /**************************************************
     * 메소드명 : getTotalPriceWithCoupon
     * 쿠폰 적용 시 최종 결제 금액 계산
     * 기능: 특정 회원(`email`)의 장바구니 총액에 지정된 쿠폰(`couponNum`)을 적용했을 때의
     * 할인된 최종 결제 금액을 계산하여 반환합니다. 쿠폰의 유효성(소유권, 만료일, 사용 여부)을 검사합니다.
     * 이 메소드는 실제 쿠폰 사용 상태를 변경하지 않습니다.
     * @param email String : 할인을 적용할 회원의 이메일.
     * @param couponNum Long : 적용할 쿠폰의 번호(ID).
     * @return Integer : 쿠폰 할인이 적용된 최종 결제 금액.
     * @throws IllegalArgumentException : 회원이 존재하지 않거나 쿠폰을 찾을 수 없는 경우.
     * @throws IllegalStateException : 쿠폰이 유효하지 않은 경우 (만료, 타인 소유, 이미 사용 등).
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-24
     * 수정일 : -
     **************************************************/

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

        if (coupon.isUsed()) {
            // 반복 사용 쿠폰인데 횟수가 0이하라면 사용 불가
            if (coupon.getRepeatCouponCount() == null || coupon.getRepeatCouponCount() <= 0) {
                throw new RuntimeException("이미 사용된 쿠폰입니다.");
            }
        }

        int originalPrice = getCartTotal(member); // 수정된 getCartTotal 사용 (옵션 포함)
        int discount = (originalPrice * coupon.getDiscountRate()) / 100;
        int finalPrice = originalPrice - discount;

        log.info("쿠폰 적용 결과: 원 가격 {}, 할인 {}, 최종 {}", originalPrice, discount, finalPrice);

        return finalPrice; // 계산된 최종 금액만 반환

    }

    /**************************************************
     * 메소드명 : addToCoupon
     * (특정 조건) 쿠폰 추가 (현재는 "생일축하쿠폰" 고정)
     * 기능: 특정 회원(`email`)에게 고정된 정보("생일축하쿠폰" 타입, 20% 할인)의 쿠폰을 생성하여 발급합니다.
     * 실제 운영 시 쿠폰 타입, 할인율, 유효기간 등은 파라미터화 하거나 정책에 따라 동적으로 설정하는 것이 좋습니다.
     * @param email String : 쿠폰을 발급받을 회원의 이메일.
     * @throws IllegalArgumentException : 해당 이메일의 회원이 존재하지 않는 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-24
     * 수정일 : -
     **************************************************/

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

    /**************************************************
     * 메소드명 : applyCoupon
     * 쿠폰 사용 처리
     * 기능: 특정 회원(`memberEmail`)이 지정된 쿠폰(`couponNum`)을 사용하는 것을 처리합니다.
     * 쿠폰의 유효성을 검사(사용 여부, 반복 사용 횟수, 만료일)한 후,
     * 쿠폰의 사용 상태를 '사용됨'으로 변경하고, 사용 시간 기록 및 반복 사용 쿠폰인 경우 횟수를 차감합니다.
     * 사용 처리된 쿠폰 정보를 `CouponDTO`로 반환합니다.
     * @param memberEmail String : 쿠폰을 사용하는 회원의 이메일.
     * @param couponNum Long : 사용할 쿠폰의 번호(ID).
     * @return CouponDTO : 사용 처리된 쿠폰의 DTO.
     * @throws IllegalArgumentException : 쿠폰을 찾을 수 없거나 사용할 수 없는 쿠폰인 경우.
     * @throws IllegalStateException : 쿠폰이 이미 사용되었거나, 사용 횟수를 초과했거나, 만료된 경우.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-24
     * 수정일 : -
     **************************************************/

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

        coupon.setUsed(true); // 단일 사용 쿠폰
        coupon.setUsedTime(LocalDateTime.now());
        if (coupon.getRepeatCouponCount() != null) {
            coupon.setRepeatCouponCount(coupon.getRepeatCouponCount() - 1);
        }

        couponRepository.save(coupon);

        return new CouponDTO(coupon);
    }

    /**************************************************
     * 메소드명 : hasCoupon
     * 특정 타입 쿠폰 소지 여부 확인 (사용 가능 기준)
     * 기능: 특정 회원(`email`)이 지정된 타입(`type`)의 사용 가능한(미사용, 유효기간 내) 쿠폰을
     * 소지하고 있는지 여부를 확인하여 boolean 값으로 반환합니다.
     * @param email String : 확인할 회원의 이메일.
     * @param type CouponType : 확인할 쿠폰의 타입.
     * @return boolean : 해당 타입의 사용 가능한 쿠폰 소지 시 true, 그렇지 않으면 false.
     * 작성자 : 김윤겸
     * 등록일 : 2025-04-27
     * 수정일 : -
     **************************************************/

    @Override
    public boolean hasCoupon(String email, CouponType type) {
        log.info("사용된 쿠폰 서비스 진입 - email: {}, type: {}", email, type);
        return couponRepository.existsByMember_MemberEmailAndCouponType(email, type);
    }


}
