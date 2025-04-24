package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByMember_MemberEmail(String memberEmail);

    // 특정 회원의 사용 가능한 쿠폰들 (예: 마이페이지용)
    List<Coupon> findByMember_MemberEmailAndExpirationDateAfterAndIsGuestFalse(String memberEmail, LocalDate today);

    // 특정 쿠폰 타입 조회 (예: 생일 쿠폰 중복 발급 방지)
    Optional<Coupon> findByMember_MemberEmailAndType(String email, String type);


    Coupon findByMember_MemberEmailAndCouponNum(String memberEmail, Long couponNum);


}
