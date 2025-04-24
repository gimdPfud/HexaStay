package com.sixthsense.hexastay.dto;

import com.sixthsense.hexastay.entity.Coupon;
import com.sixthsense.hexastay.entity.Member;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CouponDTO {

        private Long couponNum;

        private String memberEmail; // 멤버의 이메일을 조회해서 쿠폰 사용가능 멤버의 pk

        private String type; // 가입첫 쿠폰, 회원 상시쿠폰 2개

        private Integer discountRate; // 할인율

        private LocalDate issueDate; // 쿠폰 발급일자.

        private LocalDate expirationDate; // 쿠폰 만료일자.

        private boolean isGuest; // 비회원 여부

        private boolean used; // 사용여부 (단일용)

        private Integer repeatCouponCount; // 반복사용 쿠폰 (횟수)

        private LocalDateTime usedTime; // 사용한 날짜

        public CouponDTO(Coupon coupon) {
                this.couponNum = coupon.getCouponNum();
                this.memberEmail = coupon.getMember().getMemberEmail();
                this.type = coupon.getType();
                this.discountRate = coupon.getDiscountRate();
                this.issueDate = coupon.getIssueDate();
                this.expirationDate = coupon.getExpirationDate();
                this.isGuest = coupon.isGuest();
                this.used = coupon.isUsed();
                this.repeatCouponCount = coupon.getRepeatCouponCount();
                this.usedTime = coupon.getUsedTime();
        }

    }
