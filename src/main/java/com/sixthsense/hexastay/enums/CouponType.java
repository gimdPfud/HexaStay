package com.sixthsense.hexastay.enums;

import lombok.Getter;

@Getter

public enum CouponType {

    BIRTHDAY("BirthdayCoupon", 0, 30), // 예시 할인율 30%
    MEMBER("MemberCoupon", 0, 5),     // 멤버 쿠폰 5% 할인
    SIGNUP("SignupCoupon", 1, 20),     // 가입 쿠폰 20% 할인
    EVENT("EventCoupon", 3, 15);       // 이벤트 쿠폰 15% 할인

    private final String code;
    private final int maxCount; // 회원당 최대 발급 가능 개수
    private final int discountRate; // <<< 할인율 필드 추가

    // 모든 필드를 초기화하는 생성자
    CouponType(String code, int maxCount, int discountRate) { // <<< discountRate 파라미터 추가
        this.code = code;
        this.maxCount = maxCount;
        this.discountRate = discountRate; // <<< discountRate 초기화
    }

    // 필요한 getter 메소드
    public String getCode() {
        return code;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public int getDiscountRate() { // <<< getDiscountRate() 메소드 추가
        return discountRate;
    }


    // 코드 문자열로 CouponType enum 상수를 찾는 헬퍼 메소드
    public static CouponType fromCode(String code) {
        for (CouponType type : CouponType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown CouponType code: " + code);
    }
}