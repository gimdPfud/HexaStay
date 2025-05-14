package com.sixthsense.hexastay.repository;

import com.sixthsense.hexastay.entity.Coupon;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.enums.CouponType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {


    Coupon findByMember_MemberEmailAndCouponNum(String memberEmail, Long couponNum);

    // 이미 쿠폰 발급 여부 확인
    boolean existsByMember_MemberEmailAndCouponType(String email, CouponType couponType);

    // 유효한 쿠폰 목록만 가져오자.
    @Query("SELECT c FROM Coupon c WHERE c.member.memberEmail = :email " +
            "AND (c.used = false OR (c.repeatCouponCount IS NOT NULL AND c.repeatCouponCount > 0)) " +
            "AND c.expirationDate >= CURRENT_DATE")
    List<Coupon> findUsableCouponsByMemberEmail(@Param("email") String email);

    // 특정 회원과 쿠폰 타입에 해당하는 쿠폰의 개수를 세는 메소드
    long countByMemberAndCouponType(Member member, CouponType couponType);


}
