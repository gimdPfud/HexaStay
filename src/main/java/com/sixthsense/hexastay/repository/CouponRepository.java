package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 이미 쿠폰 발급 여부 확인
    boolean existsByMember_MemberEmailAndType(String memberEmail, String type);

    // 유효한 쿠폰 목록만 가져오자.
    @Query("SELECT c FROM Coupon c WHERE c.member.memberEmail = :email " +
            "AND (c.used = false OR (c.repeatCouponCount IS NOT NULL AND c.repeatCouponCount > 0)) " +
            "AND c.expirationDate >= CURRENT_DATE")
    List<Coupon> findUsableCouponsByMemberEmail(@Param("email") String email);


}
